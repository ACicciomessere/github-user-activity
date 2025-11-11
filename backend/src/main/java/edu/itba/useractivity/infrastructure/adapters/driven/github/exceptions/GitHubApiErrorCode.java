package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * * Basado en la documentación de GitHub: https://docs.github.com/es/rest/guides/handling-errors
 */
public enum GitHubApiErrorCode {

    /**
     * 400 Bad Request: La solicitud fue malformada.
     */
    BAD_REQUEST(400),

    /**
     * 401 Unauthorized: Requiere autenticación.
     * La solicitud no proveyó un token válido.
     */
    UNAUTHORIZED(401),

    /**
     * 403 Forbidden: El cliente no tiene permisos O ha excedido el rate limit.
     * En nuestro caso, lo tratamos principalmente como Rate Limit.
     */
    FORBIDDEN(403),

    /**
     * 404 Not Found: El recurso solicitado (usuario, repo) no existe.
     */
    NOT_FOUND(404),

    /**
     * 422 Unprocessable Entity: Falla de validación.
     * El JSON enviado era sintácticamente correcto, pero semánticamente incorrecto.
     */
    UNPROCESSABLE_ENTITY(422);


    private final int code;

    private static final Map<Integer, GitHubApiErrorCode> CODE_MAP =
            Stream.of(values()).collect(Collectors.toMap(e -> e.code, Function.identity()));

    GitHubApiErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Optional<GitHubApiErrorCode> fromStatusCode(int code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    public static boolean isServerError(int code) {
        return code >= 500 && code <= 599;
    }
}
