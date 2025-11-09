package edu.itba.useractivity.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Repository {
    private Long id;
    private String name;
    private String fullName;
    private String htmlUrl;
    private String description;
    private Boolean isPrivate;
    private User owner;
}
