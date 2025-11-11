package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubApiExceptionTest {

    @Test
    @DisplayName("Constructor asigna correctamente statusCode, apiMessage y genera mensaje completo")
    void constructor_assignsFieldsCorrectly() {
        int status = 500;
        String apiMsg = "GitHub API server error";

        GitHubApiException ex = new GitHubApiException(status, apiMsg);

        // Verificamos el mensaje completo generado por el super()
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("GitHub API error: " + apiMsg + " (status " + status + ")");

        // Verificamos getters
        assertThat(ex.getStatusCode()).isEqualTo(status);
        assertThat(ex.getApiMessage()).isEqualTo(apiMsg);
    }

    @Test
    @DisplayName("Constructor permite status y mensaje distintos sin afectar formato")
    void constructor_variedInputs_stillFormatsMessage() {
        GitHubApiException ex = new GitHubApiException(404, "Not Found");

        assertThat(ex.getStatusCode()).isEqualTo(404);
        assertThat(ex.getApiMessage()).isEqualTo("Not Found");
        assertThat(ex.getMessage()).isEqualTo("GitHub API error: Not Found (status 404)");
    }
}
