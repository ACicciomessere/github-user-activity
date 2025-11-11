package integration;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.ports.inbound.EventInboundPort;
import edu.itba.useractivity.infrastructure.adapters.driving.EventController;
import edu.itba.useractivity.infrastructure.adapters.driving.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventController.class)
@Import(GlobalExceptionHandler.class)
class EventControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventInboundPort eventInboundPort;

    @Test
    @DisplayName("GET /events/user/{username} → 200 (lista vacía OK)")
    void getEvents_ok_empty() throws Exception {
        Mockito.when(eventInboundPort.getUserEvents("roni", 1, 30))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/events/user/roni"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /events/{type}/user/{username} → 200 (tipo válido, lista vacía OK)")
    void getEventsByType_ok_empty() throws Exception {
        Mockito.when(eventInboundPort.getUserEventsByType(EventType.PUSH, "roni", 2, 10))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/events/PushEvent/user/roni")
                        .param("page", "2")
                        .param("per_page", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /events/{type}/user/{username} con type inválido → 400 (handler)")
    void getEventsByType_invalidType() throws Exception {
        mvc.perform(get("/events/NotARealType/user/roni"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
