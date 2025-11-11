package domain.exceptions;

import edu.itba.useractivity.domain.exceptions.InvalidEventTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidEventTypeExceptionTest {

    @Test
    @DisplayName("Construye con nombre y expone el mensaje esperado")
    void message_withName() {
        InvalidEventTypeException ex = new InvalidEventTypeException("PushEvent");
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unknown or invalid event name: PushEvent");
    }

    @Test
    @DisplayName("Construye con null y el mensaje incluye 'null'")
    void message_withNull() {
        InvalidEventTypeException ex = new InvalidEventTypeException(null);
        assertThat(ex.getMessage())
                .isEqualTo("Unknown or invalid event name: null");
    }
}
