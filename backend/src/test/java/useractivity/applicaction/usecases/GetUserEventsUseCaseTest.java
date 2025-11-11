package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetUserEventsUseCase;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetUserEventsUseCaseTest {

    @Test
    @DisplayName("execute delega en EventOutboundPort.getEventsByUser y devuelve su resultado")
    void execute_delegates() {
        // Arrange
        EventOutboundPort port = mock(EventOutboundPort.class);
        GetUserEventsUseCase useCase = new GetUserEventsUseCase(port);

        String username = "alice";
        int page = 1, perPage = 20;

        Event e1 = mock(Event.class), e2 = mock(Event.class);
        List<Event> expected = List.of(e1, e2);

        when(port.getEventsByUser(username, page, perPage)).thenReturn(expected);

        // Act
        List<Event> result = useCase.execute(username, page, perPage);

        // Assert
        assertThat(result).isSameAs(expected);
        verify(port).getEventsByUser(eq(username), eq(page), eq(perPage));
        verifyNoMoreInteractions(port);
    }
}
