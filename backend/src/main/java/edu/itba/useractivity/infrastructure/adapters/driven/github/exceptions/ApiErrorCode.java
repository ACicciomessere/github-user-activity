package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ApiErrorCode {

    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    UNPROCESSABLE_ENTITY(422);


    private final int code;

    private static final Map<Integer, ApiErrorCode> CODE_MAP =
            Stream.of(values()).collect(Collectors.toMap(e -> e.code, Function.identity()));

    ApiErrorCode(int code) {
        this.code = code;
    }

    public static Optional<ApiErrorCode> fromStatusCode(int code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    public static boolean isServerError(int code) {
        return code >= 500 && code <= 599;
    }
}
