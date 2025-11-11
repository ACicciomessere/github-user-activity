package infraestructure.adapters.driven.github.exceptions;

import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ApiErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorCodeTest {

    @Test
    @DisplayName("getCode y fromStatusCode: mapeo 1-1 para todos los códigos conocidos")
    void mapping_allKnownCodes_roundtrip() {
        for (ApiErrorCode code : ApiErrorCode.values()) {
            assertThat(ApiErrorCode.fromStatusCode(code.getCode()))
                    .as("fromStatusCode(%s)", code.getCode())
                    .contains(code);
        }

        // chequeo explícito de algunos
        assertThat(ApiErrorCode.BAD_REQUEST.getCode()).isEqualTo(400);
        assertThat(ApiErrorCode.UNAUTHORIZED.getCode()).isEqualTo(401);
        assertThat(ApiErrorCode.FORBIDDEN.getCode()).isEqualTo(403);
        assertThat(ApiErrorCode.NOT_FOUND.getCode()).isEqualTo(404);
        assertThat(ApiErrorCode.UNPROCESSABLE_ENTITY.getCode()).isEqualTo(422);
    }

    @Test
    @DisplayName("fromStatusCode: código desconocido devuelve Optional.empty()")
    void fromStatusCode_unknown() {
        assertThat(ApiErrorCode.fromStatusCode(418)).isEmpty(); // I'm a teapot 😉
        assertThat(ApiErrorCode.fromStatusCode(999)).isEmpty();
        assertThat(ApiErrorCode.fromStatusCode(-1)).isEmpty();
    }

    @Test
    @DisplayName("isServerError: true en [500..599], false fuera de ese rango (incluye límites)")
    void isServerError_bounds() {
        // límites
        assertThat(ApiErrorCode.isServerError(500)).isTrue();
        assertThat(ApiErrorCode.isServerError(599)).isTrue();

        // justo afuera
        assertThat(ApiErrorCode.isServerError(499)).isFalse();
        assertThat(ApiErrorCode.isServerError(600)).isFalse();

        // algunos valores del medio
        assertThat(ApiErrorCode.isServerError(501)).isTrue();
        assertThat(ApiErrorCode.isServerError(550)).isTrue();
    }
}
