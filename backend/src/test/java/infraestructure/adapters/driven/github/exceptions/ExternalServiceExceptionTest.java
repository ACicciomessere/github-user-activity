package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ExternalServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ExternalServiceExceptionTest {

    @Test
    @DisplayName("Constructor(message) guarda el mensaje y no tiene causa")
    void ctor_message_only() {
        ExternalServiceException ex = new ExternalServiceException("boom");
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom")
                .hasNoCause();
    }

    @Test
    @DisplayName("Constructor(message, cause) guarda mensaje y causa")
    void ctor_message_with_cause() {
        Throwable cause = new IllegalStateException("root");
        ExternalServiceException ex = new ExternalServiceException("wrap", cause);

        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("wrap")
                .hasCause(cause);
        assertThat(ex.getCause())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("root");
    }
}
