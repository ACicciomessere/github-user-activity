package infraestructure.adapters.driving;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.exceptions.InvalidEventTypeException;
import edu.itba.useractivity.domain.ports.inbound.EventInboundPort;
import edu.itba.useractivity.infrastructure.adapters.driving.EventController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @Test
    @DisplayName("GET /events/user/{username}: devuelve 200 y la lista del inbound port")
    void getEvents_ok() {
        // given
        EventInboundPort port = mock(EventInboundPort.class);
        EventController controller = new EventController(port);

        String username = "alice";
        int page = 2;
        int perPage = 5;

        Event e1 = mock(Event.class);
        Event e2 = mock(Event.class);
        List<Event> expected = List.of(e1, e2);

        when(port.getUserEvents(username, page, perPage)).thenReturn(expected);

        // when
        var response = controller.getEvents(username, page, perPage);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(port).getUserEvents(username, page, perPage);
        verifyNoMoreInteractions(port);
    }

    @Test
    @DisplayName("GET /events/{eventType}/user/{username}: tipo válido → 200 y body OK")
    void getEventsByType_ok() {
        // given
        EventInboundPort port = mock(EventInboundPort.class);
        EventController controller = new EventController(port);

        // Usa el string que tu enum espera en fromEventName(...)
        // Si tu enum mapea nombres de GitHub, suele ser "PushEvent", "ForkEvent", etc.
        String eventTypeStr = "PushEvent";
        String username = "bob";
        int page = 1;
        int perPage = 30;

        Event ev = mock(Event.class);
        List<Event> expected = List.of(ev);

        when(port.getUserEventsByType(any(EventType.class), eq(username), eq(page), eq(perPage)))
                .thenReturn(expected);

        // when
        var response = controller.getEventsByType(eventTypeStr, username, page, perPage);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(port).getUserEventsByType(any(EventType.class), eq(username), eq(page), eq(perPage));
        verifyNoMoreInteractions(port);
    }

    @Test
    @DisplayName("GET /events/{eventType}/user/{username}: tipo inválido → InvalidEventTypeException")
    void getEventsByType_invalidType_throws() {
        // given
        EventInboundPort port = mock(EventInboundPort.class);
        EventController controller = new EventController(port);

        // when / then
        assertThatThrownBy(() -> controller.getEventsByType("not-a-valid-type", "roni", 1, 30))
                .isInstanceOf(InvalidEventTypeException.class)
                .hasMessageContaining("Unknown or invalid event name");
        verifyNoInteractions(port);
    }

    @Test
    @DisplayName("GET /events/{eventType}/user/{username}: si fromEventName devuelve null → IllegalArgumentException (cubre rama faltante)")
    void getEventsByType_nullFromEnum_throwsIllegalArgument() {
        EventInboundPort port = mock(EventInboundPort.class);
        EventController controller = new EventController(port);

        try (MockedStatic<EventType> mocked = Mockito.mockStatic(EventType.class)) {
            mocked.when(() -> EventType.fromEventName("nullish")).thenReturn(null);

            assertThatThrownBy(() -> controller.getEventsByType("nullish", "neo", 1, 30))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid event type");
        }

        verifyNoInteractions(port);
    }

}
