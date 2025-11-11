package domain.exceptions;

import edu.itba.useractivity.domain.exceptions.InvalidPullRequestActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidPullRequestActionExceptionTest {

    @Test
    @DisplayName("Construye con acción y genera el mensaje esperado")
    void message_withAction() {
        InvalidPullRequestActionException ex = new InvalidPullRequestActionException("merge");
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unknown or invalid pull request action: merge");
    }

    @Test
    @DisplayName("Construye con null y el mensaje incluye 'null'")
    void message_withNull() {
        InvalidPullRequestActionException ex = new InvalidPullRequestActionException(null);
        assertThat(ex.getMessage())
                .isEqualTo("Unknown or invalid pull request action: null");
    }
}
