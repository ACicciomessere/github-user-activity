package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubClientExceptionTest {

    @Test
    @DisplayName("Constructor delega correctamente a GitHubApiException y conserva campos")
    void constructor_assignsProperly() {
        int status = 401;
        String message = "Unauthorized request";

        GitHubClientException ex = new GitHubClientException(status, message);

        // Verificamos jerarquía
        assertThat(ex)
                .isInstanceOf(GitHubClientException.class)
                .hasMessage("GitHub API error: " + message + " (status " + status + ")");

        // Verificamos campos heredados de GitHubApiException
        assertThat(ex.getStatusCode()).isEqualTo(status);
        assertThat(ex.getApiMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Constructor soporta distintos códigos y mensajes sin romper el formato")
    void constructor_variedInputs_stillFormatsMessage() {
        GitHubClientException ex = new GitHubClientException(400, "Bad Request");

        assertThat(ex.getStatusCode()).isEqualTo(400);
        assertThat(ex.getApiMessage()).isEqualTo("Bad Request");
        assertThat(ex.getMessage()).isEqualTo("GitHub API error: Bad Request (status 400)");
    }
}
