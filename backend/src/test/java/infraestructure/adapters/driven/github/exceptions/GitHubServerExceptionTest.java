package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubServerExceptionTest {

    @Test
    @DisplayName("Constructor asigna correctamente el mensaje y los campos heredados")
    void constructor_assignsProperly() {
        int status = 503;
        String msg = "GitHub service unavailable";

        GitHubServerException ex = new GitHubServerException(status, msg);

        assertThat(ex)
                .isInstanceOf(GitHubServerException.class)
                .hasMessage("GitHub API error: " + msg + " (status " + status + ")");

        assertThat(ex.getStatusCode()).isEqualTo(status);
        assertThat(ex.getApiMessage()).isEqualTo(msg);
    }

    @Test
    @DisplayName("Constructor mantiene formato de mensaje con códigos y textos distintos")
    void constructor_variedInputs_stillFormatsMessage() {
        GitHubServerException ex = new GitHubServerException(500, "Internal Error");

        assertThat(ex.getStatusCode()).isEqualTo(500);
        assertThat(ex.getApiMessage()).isEqualTo("Internal Error");
        assertThat(ex.getMessage()).isEqualTo("GitHub API error: Internal Error (status 500)");
    }
}
