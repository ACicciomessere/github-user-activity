package edu.itba.useractivity.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class Commit {
    private String sha;
    private String message;
    private String authorName;
    private ZonedDateTime date;
    private String htmlUrl;
}
