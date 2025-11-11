// java
package domain.enums;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.exceptions.InvalidEventTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EventTypeTest {

    @Test
    @DisplayName("Cada constante expone el eventName correcto")
    void getters_ok() {
        assertThat(EventType.PUSH.getEventName()).isEqualTo("PushEvent");
        assertThat(EventType.PULL_REQUEST.getEventName()).isEqualTo("PullRequestEvent");
        assertThat(EventType.FORK.getEventName()).isEqualTo("ForkEvent");
        assertThat(EventType.CREATE.getEventName()).isEqualTo("CreateEvent");
    }

    @ParameterizedTest
    @CsvSource({
            "PushEvent,PUSH",
            "pushEvent,PUSH",
            "PULLREQUESTEVENT,PULL_REQUEST",
            "pullrequestevent,PULL_REQUEST",
            "FORKEVENT,FORK",
            "forkevent,FORK",
            "createevent,CREATE",
            "CreateEvent,CREATE"
    })
    @DisplayName("fromEventName: válidos (case-insensitive) mapea correctamente a cada constante")
    void fromEventName_valid_caseInsensitive(String input, String expectedEnumName) {
        EventType expected = EventType.valueOf(expectedEnumName);
        assertThat(EventType.fromEventName(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("fromEventName: valores exactos para todas las constantes (cubre el stream completo)")
    void fromEventName_forAllValues() {
        for (EventType type : EventType.values()) {
            assertThat(EventType.fromEventName(type.getEventName())).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("fromEventName: null y blank lanzan InvalidEventTypeException")
    void fromEventName_null_blank_throw() {
        assertThatThrownBy(() -> EventType.fromEventName(null))
                .isInstanceOf(InvalidEventTypeException.class)
                .hasMessageContaining("null");

        assertThatThrownBy(() -> EventType.fromEventName(""))
                .isInstanceOf(InvalidEventTypeException.class);

        assertThatThrownBy(() -> EventType.fromEventName("   "))
                .isInstanceOf(InvalidEventTypeException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SomethingElse", "UnknownEvent", "PushEvnt"})
    @DisplayName("fromEventName: valor desconocido lanza InvalidEventTypeException con el nombre en el mensaje")
    void fromEventName_unknown_throw(String unknown) {
        assertThatThrownBy(() -> EventType.fromEventName(unknown))
                .isInstanceOf(InvalidEventTypeException.class)
                .hasMessageContaining(unknown);
    }

    @Test
    @DisplayName("values() y valueOf() funcionan correctamente y valueOf desconocido lanza IllegalArgumentException")
    void values_valueOf_ok_and_valueOf_unknown() {
        assertThat(EventType.valueOf("PUSH")).isEqualTo(EventType.PUSH);
        assertThat(EventType.values())
                .containsExactlyInAnyOrder(EventType.PUSH, EventType.PULL_REQUEST, EventType.FORK, EventType.CREATE);

        assertThatThrownBy(() -> EventType.valueOf("NOT_A_VALUE"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}