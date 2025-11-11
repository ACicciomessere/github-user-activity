package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.RateLimitExceededException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitExceededExceptionTest {

    @Test
    @DisplayName("Constructor asigna correctamente el mensaje de error")
    void constructor_assignsMessage() {
        String msg = "GitHub API rate limit exceeded";

        RateLimitExceededException ex = new RateLimitExceededException(msg);

        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage(msg);
    }

    @Test
    @DisplayName("Constructor soporta mensajes vacíos o nulos sin lanzar excepción")
    void constructor_allowsNullOrEmptyMessages() {
        RateLimitExceededException ex1 = new RateLimitExceededException("");
        RateLimitExceededException ex2 = new RateLimitExceededException(null);

        assertThat(ex1.getMessage()).isEmpty();
        assertThat(ex2.getMessage()).isNull();
    }
}
