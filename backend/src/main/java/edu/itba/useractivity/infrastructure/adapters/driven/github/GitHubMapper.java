package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import edu.itba.useractivity.domain.models.*;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class GitHubMapper {

    public List<Event> mapToEvents(JsonNode rootNode) {
        return StreamSupport.stream(rootNode.spliterator(), false)
                .map(this::mapToEvent)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public List<PullRequest> mapToPullRequests(JsonNode rootNode) {
        return StreamSupport.stream(rootNode.spliterator(), false)
                .map(this::mapToPullRequest)
                .collect(Collectors.toList());
    }

    public List<Commit> mapToCommits(JsonNode rootNode) {
        return StreamSupport.stream(rootNode.spliterator(), false)
                .map(this::mapToCommit)
                .collect(Collectors.toList());
    }

    public Commit mapToCommit(JsonNode node) {
        JsonNode commitNode = node.path("commit");
        JsonNode authorNode = commitNode.path("author");

        return new Commit(
                node.path("sha").asText(),
                commitNode.path("message").asText(),
                authorNode.path("name").asText(),
                ZonedDateTime.parse(authorNode.path("date").asText()),
                node.path("html_url").asText()
        );
    }

    public User mapToUser(JsonNode node) {
        return new User(
                node.path("id").asLong(),
                node.path("login").asText(),
                node.path("avatar_url").asText(),
                node.path("html_url").asText(),
                node.path("type").asText()
        );
    }

    public Repository mapToRepository(JsonNode node) {
        return new Repository(
                node.path("id").asLong(),
                node.path("name").asText(),
                node.path("full_name").asText(),
                node.path("html_url").asText(),
                node.path("description").asText(),
                node.path("private").asBoolean(false),
                node.has("owner") ? mapToUser(node.path("owner")) : null
        );
    }

    public PullRequest mapToPullRequest(JsonNode node) {
        return new PullRequest(
                node.path("id").asLong(),
                node.path("number").asInt(),
                node.path("title").asText(),
                node.path("state").asText(),
                mapToUser(node.path("user")),
                ZonedDateTime.parse(node.path("created_at").asText()),
                ZonedDateTime.parse(node.path("updated_at").asText()),
                node.has("closed_at") && !node.get("closed_at").isNull() ? ZonedDateTime.parse(node.path("closed_at").asText()) : null,
                node.has("merged_at") && !node.get("merged_at").isNull() ? ZonedDateTime.parse(node.path("merged_at").asText()) : null,
                node.path("html_url").asText()
        );
    }

    private Optional<Event> mapToEvent(JsonNode node) {
        String type = node.path("type").asText();

        return switch (type) {
            case "PushEvent" -> Optional.of(mapToPushEvent(node));
            case "PullRequestEvent" -> Optional.of(mapToPullRequestEvent(node));
            case "ForkEvent" -> Optional.of(mapToForkEvent(node));
            case "CreateEvent" -> Optional.of(mapToCreateEvent(node));
            default -> Optional.empty();
        };
    }

    private PushEvent mapToPushEvent(JsonNode node) {
        JsonNode payload = node.path("payload");
        List<Commit> commits = StreamSupport.stream(payload.path("commits").spliterator(), false)
                .map(this::mapToCommit)
                .collect(Collectors.toList());

        return new PushEvent(
                node.path("id").asText(),
                mapToUser(node.path("actor")),
                mapToRepository(node.path("repo")),
                ZonedDateTime.parse(node.path("created_at").asText()),
                payload.path("ref").asText(),
                payload.path("before").asText(),
                payload.path("head").asText(),
                commits
        );
    }

    private PullRequestEvent mapToPullRequestEvent(JsonNode node) {
        return new PullRequestEvent(
                node.path("id").asText(),
                mapToUser(node.path("actor")),
                mapToRepository(node.path("repo")),
                ZonedDateTime.parse(node.path("created_at").asText()),
                PullRequestAction.fromString(node.path("payload").get("action").asText()),
                mapToPullRequest(node.path("payload").get("pull_request"))
        );
    }

    private ForkEvent mapToForkEvent(JsonNode node) {
        return new ForkEvent(
                node.path("id").asText(),
                mapToUser(node.path("actor")),
                mapToRepository(node.path("repo")),
                ZonedDateTime.parse(node.path("created_at").asText()),
                mapToRepository(node.path("payload").path("forkee"))
        );
    }

    private CreateEvent mapToCreateEvent(JsonNode node) {
        JsonNode payload = node.path("payload");
        return new CreateEvent(
                node.path("id").asText(),
                mapToUser(node.path("actor")),
                mapToRepository(node.path("repo")),
                ZonedDateTime.parse(node.path("created_at").asText()),
                payload.path("ref_type").asText(),
                payload.path("ref").asText(),
                payload.path("master_branch").asText(),
                payload.path("description").asText()
        );
    }
}

