package edu.itba.useractivity.domain.models;

public record Repository (
    Long id,
    String name,
    String fullName,
    String htmlUrl,
    String description,
    Boolean isPrivate,
    User owner
){}
