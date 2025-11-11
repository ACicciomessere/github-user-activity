package integration;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.ports.inbound.EventInboundPort;
import edu.itba.useractivity.domain.ports.inbound.RepositoryInboundPort;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.*;
import edu.itba.useractivity.infrastructure.adapters.driving.EventController;
import edu.itba.useractivity.infrastructure.adapters.driving.RepositoryController;
import edu.itba.useractivity.infrastructure.adapters.driving.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {EventController.class, RepositoryController.class})
@Import(GlobalExceptionHandler.class)
class ErrorMappingIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventInboundPort eventInboundPort;

    @MockBean
    private RepositoryInboundPort repositoryInboundPort;

    @Nested
    class GitHubErrors {
        @Test
        @DisplayName("ResourceNotFoundException → 404")
        void notFound() throws Exception {
            Mockito.when(repositoryInboundPort.getCommits(anyString(), anyString(), anyInt(), anyInt()))
                    .thenThrow(new ResourceNotFoundException("not found"));
            mvc.perform(get("/repository/itba/repo/commits"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("GitHubClientException → 400")
        void clientError() throws Exception {
            Mockito.when(repositoryInboundPort.getPullRequests(anyString(), anyString(), anyInt(), anyInt()))
                    .thenThrow(new GitHubClientException(400, "bad request"));
            mvc.perform(get("/repository/itba/repo/pull-requests"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("GitHubServerException → 502")
        void serverError() throws Exception {
            Mockito.when(repositoryInboundPort.getMergedPullRequests(anyString(), anyString(), anyInt(), anyInt()))
                    .thenThrow(new GitHubServerException(500, "server"));
            mvc.perform(get("/repository/itba/repo/pull-requests/merged"))
                    .andExpect(status().isBadGateway())
                    .andExpect(jsonPath("$.status").value(502));
        }

        @Test
        @DisplayName("RateLimitExceededException → 429")
        void rateLimit() throws Exception {
            Mockito.when(repositoryInboundPort.getPullRequestsLifeAvg(anyString(), anyString()))
                    .thenThrow(new RateLimitExceededException("rate"));
            mvc.perform(get("/repository/itba/repo/pull-requests/life-avg"))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.status").value(429));
        }
    }

    @Nested
    class DomainErrors {
        @Test
        @DisplayName("InvalidEventTypeException → 500 (sin handler específico)")
        void invalidEventType() throws Exception {
            Mockito.when(eventInboundPort.getUserEventsByType(eq(EventType.PUSH), anyString(), anyInt(), anyInt()))
                    .thenThrow(new InvalidEventTypeException("PushEvent"));

            mvc.perform(get("/events/PushEvent/user/roni"))
                    .andExpect(status().isInternalServerError())         // 500 actual
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }

        @Test
        @DisplayName("ExternalServiceException → 502 (comportamiento actual)")
        void externalService() throws Exception {
            Mockito.when(eventInboundPort.getUserEvents(anyString(), anyInt(), anyInt()))
                    .thenThrow(new ExternalServiceException("downstream", null));

            mvc.perform(get("/events/user/roni"))
                    .andExpect(status().isBadGateway())                  // 502 actual
                    .andExpect(jsonPath("$.status").value(502))
                    .andExpect(jsonPath("$.error").value("Bad Gateway"));
        }

        @Test
        @DisplayName("Excepción genérica → 500")
        void generic() throws Exception {
            Mockito.when(eventInboundPort.getUserEvents(anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("boom"));
            mvc.perform(get("/events/user/roni"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500));
        }
    }
}
