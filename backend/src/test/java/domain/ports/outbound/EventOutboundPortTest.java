package domain.ports.outbound;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EventOutboundPortTest {

    @Test
    @DisplayName("Implementación anónima ejecuta correctamente getEventsByUser")
    void anonymousImplementation_executesMethod() {
        Event e1 = mock(Event.class);
        Event e2 = mock(Event.class);

        EventOutboundPort port = new EventOutboundPort() {
            @Override
            public List<Event> getEventsByUser(String username, int page, int perPage) {
                return List.of(e1, e2);
            }
        };

        List<Event> result = port.getEventsByUser("roni", 1, 20);

        assertThat(result)
                .isNotEmpty()
                .containsExactly(e1, e2);
    }
}
