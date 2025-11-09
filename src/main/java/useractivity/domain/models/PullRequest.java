package useractivity.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class PullRequest {
    private Long id;
    private Integer number;
    private String title;
    private String state;
    private User user;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime closedAt;
    private ZonedDateTime mergedAt;
    private String htmlUrl;
}