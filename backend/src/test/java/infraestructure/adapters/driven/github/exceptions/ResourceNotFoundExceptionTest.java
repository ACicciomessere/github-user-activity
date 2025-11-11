package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Constructor asigna correctamente el mensaje y el código 404")
    void constructor_assignsProperly() {
        String msg = "Repository not found";

        ResourceNotFoundException ex = new ResourceNotFoundException(msg);

        assertThat(ex)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("GitHub API error: " + msg + " (status 404)");

        assertThat(ex.getStatusCode()).isEqualTo(404);
        assertThat(ex.getApiMessage()).isEqualTo(msg);
    }

    @Test
    @DisplayName("Constructor permite mensajes vacíos o nulos sin romper el formato")
    void constructor_allowsNullOrEmptyMessages() {
        ResourceNotFoundException ex1 = new ResourceNotFoundException("");
        ResourceNotFoundException ex2 = new ResourceNotFoundException(null);

        assertThat(ex1.getMessage()).isEqualTo("GitHub API error:  (status 404)");
        assertThat(ex1.getStatusCode()).isEqualTo(404);

        assertThat(ex2.getMessage()).isEqualTo("GitHub API error: null (status 404)");
        assertThat(ex2.getStatusCode()).isEqualTo(404);
    }
}
