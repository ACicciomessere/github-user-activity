package domain.ports.inbound;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.inbound.EventInboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventInboundPortTest {

    @Test
    @DisplayName("Implementación anónima cumple contrato y devuelve listas")
    void anonymousImplementation_executesMethods() {
        EventInboundPort port = new EventInboundPort() {
            @Override
            public List<Event> getUserEvents(String username, int page, int perPage) {
                return List.of(); // simulamos respuesta vacía
            }

            @Override
            public List<Event> getUserEventsByType(EventType type, String username, int page, int perPage) {
                return List.of();
            }
        };

        List<Event> all = port.getUserEvents("roni", 1, 10);
        List<Event> filtered = port.getUserEventsByType(EventType.PUSH, "roni", 1, 10);

        assertThat(all).isEmpty();
        assertThat(filtered).isEmpty();
    }
}
