package org.example.domain.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Commit {
    private String sha;
    private String message;
    private String url;
}
