package infraestructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.enums.PullRequestAction;
import edu.itba.useractivity.domain.models.*;
import edu.itba.useractivity.infrastructure.adapters.driven.github.GitHubMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubMapperTest {

    private final ObjectMapper om = new ObjectMapper();
    private final GitHubMapper mapper = new GitHubMapper();

    @Test
    @DisplayName("mapToUser debería mapear campos básicos")
    void mapToUser_ok() throws Exception {
        String json = """
        { "id":7, "login":"simon", "avatar_url":"https://a", "html_url":"https://p", "type":"User" }
        """;
        JsonNode node = om.readTree(json);
        User u = mapper.mapToUser(node);
        assertThat(u.id()).isEqualTo(7L);
        assertThat(u.username()).isEqualTo("simon");
        assertThat(u.avatarUrl()).isEqualTo("https://a");
        assertThat(u.profileUrl()).isEqualTo("https://p");
        assertThat(u.type()).isEqualTo("User");
    }

    @Test
    @DisplayName("mapToRepository debería mapear owner cuando está presente")
    void mapToRepository_ok() throws Exception {
        String json = """
        {
          "id": 10,
          "name": "repo",
          "full_name": "itba/repo",
          "html_url": "https://r",
          "description": "d",
          "private": false,
          "owner": { "id":1, "login":"owner", "avatar_url":"a", "html_url":"p", "type":"User" }
        }
        """;
        JsonNode node = om.readTree(json);
        Repository r = mapper.mapToRepository(node);
        assertThat(r.id()).isEqualTo(10L);
        assertThat(r.name()).isEqualTo("repo");
        assertThat(r.fullName()).isEqualTo("itba/repo");
        assertThat(r.htmlUrl()).isEqualTo("https://r");
        assertThat(r.description()).isEqualTo("d");
        assertThat(r.isPrivate()).isFalse();
        assertThat(r.owner()).isNotNull();
        assertThat(r.owner().username()).isEqualTo("owner");
    }

    @Test
    @DisplayName("mapToRepository debería dejar owner en null cuando no está presente")
    void mapToRepository_noOwner() throws Exception {
        String json = """
        {
          "id": 11,
          "name": "repo2",
          "full_name": "itba/repo2",
          "html_url": "https://r2",
          "description": "d2",
          "private": true
        }
        """;
        JsonNode node = om.readTree(json);
        Repository r = mapper.mapToRepository(node);
        assertThat(r.id()).isEqualTo(11L);
        assertThat(r.owner()).isNull();
        assertThat(r.isPrivate()).isTrue();
    }

    @Test
    @DisplayName("mapToCommit debería mapear autor, fecha y url")
    void mapToCommit_ok() throws Exception {
        String json = """
        {
          "sha": "abc",
          "commit": {
            "message": "msg",
            "author": { "name": "Roni", "date": "2025-01-01T12:00:00Z" }
          },
          "html_url": "https://c"
        }
        """;
        JsonNode node = om.readTree(json);
        Commit c = mapper.mapToCommit(node);
        assertThat(c.sha()).isEqualTo("abc");
        assertThat(c.message()).isEqualTo("msg");
        assertThat(c.authorName()).isEqualTo("Roni");
        assertThat(c.date()).isEqualTo(ZonedDateTime.parse("2025-01-01T12:00:00Z"));
        assertThat(c.htmlUrl()).isEqualTo("https://c");
    }

    @Test
    @DisplayName("mapToPullRequest debe tolerar closed_at/merged_at nulos")
    void mapToPullRequest_nullDates() throws Exception {
        String json = """
        {
          "id": 2, "number": 33, "title": "fix", "state": "open",
          "user": { "id": 7, "login": "simon", "avatar_url": "", "html_url": "", "type": "User" },
          "created_at": "2025-01-02T10:00:00Z",
          "updated_at": "2025-01-02T11:00:00Z",
          "closed_at": null,
          "merged_at": null,
          "html_url": "https://pr"
        }
        """;
        JsonNode node = om.readTree(json);
        PullRequest pr = mapper.mapToPullRequest(node);
        assertThat(pr.getId()).isEqualTo(2L);
        assertThat(pr.getNumber()).isEqualTo(33);
        assertThat(pr.getTitle()).isEqualTo("fix");
        assertThat(pr.getState()).isEqualTo("open");
        assertThat(pr.getUser()).isNotNull();
        assertThat(pr.getCreatedAt()).isEqualTo(ZonedDateTime.parse("2025-01-02T10:00:00Z"));
        assertThat(pr.getUpdatedAt()).isEqualTo(ZonedDateTime.parse("2025-01-02T11:00:00Z"));
        assertThat(pr.getClosedAt()).isNull();
        assertThat(pr.getMergedAt()).isNull();
        assertThat(pr.isMerged()).isFalse();
        assertThat(pr.getHtmlUrl()).isEqualTo("https://pr");
    }

    @Test
    @DisplayName("mapToPullRequest debe parsear closed_at/merged_at cuando vienen")
    void mapToPullRequest_withDates() throws Exception {
        String json = """
        {
          "id": 3, "number": 34, "title": "merge me", "state": "closed",
          "user": { "id": 1, "login": "u", "avatar_url": "", "html_url": "", "type": "User" },
          "created_at": "2025-01-03T10:00:00Z",
          "updated_at": "2025-01-03T11:00:00Z",
          "closed_at": "2025-01-03T10:59:00Z",
          "merged_at": "2025-01-03T10:59:00Z",
          "html_url": "pr2-url"
        }
        """;
        JsonNode node = om.readTree(json);
        PullRequest pr = mapper.mapToPullRequest(node);
        assertThat(pr.getClosedAt()).isEqualTo(ZonedDateTime.parse("2025-01-03T10:59:00Z"));
        assertThat(pr.getMergedAt()).isEqualTo(ZonedDateTime.parse("2025-01-03T10:59:00Z"));
        assertThat(pr.isMerged()).isTrue();
    }

    @Test
    @DisplayName("mapToPushEvent debería mapear ref/head/before y commits")
    void mapToPushEvent_ok() throws Exception {
        String json = """
        {
          "id": "e1",
          "type": "PushEvent",
          "actor": { "id": 7, "login": "simon", "avatar_url": "", "html_url": "", "type": "User" },
          "repo": { "id": 10, "name": "r", "full_name": "itba/r", "html_url": "h", "description": "", "private": false },
          "created_at": "2025-01-01T00:00:00Z",
          "payload": {
            "ref": "refs/heads/main",
            "before": "aaaa",
            "head": "bbbb",
            "commits": [
              { "sha": "1", "commit": {"message": "m1", "author":{"name":"A","date":"2025-01-01T00:00:01Z"}}, "html_url":"u1"},
              { "sha": "2", "commit": {"message": "m2", "author":{"name":"B","date":"2025-01-01T00:00:02Z"}}, "html_url":"u2"}
            ]
          }
        }
        """;
        JsonNode node = om.readTree(json);
        PushEvent ev = mapper.mapToPushEvent(node);
        assertThat(ev.getId()).isEqualTo("e1");
        assertThat(ev.getType()).isEqualTo(EventType.PUSH);
        assertThat(ev.getRef()).isEqualTo("refs/heads/main");
        assertThat(ev.getBefore()).isEqualTo("aaaa");
        assertThat(ev.getHead()).isEqualTo("bbbb");
        assertThat(ev.getCommits()).hasSize(2);
        assertThat(ev.getCommits().get(0).sha()).isEqualTo("1");
        assertThat(ev.getCommits().get(1).message()).isEqualTo("m2");
    }

    @Test
    @DisplayName("mapToForkEvent debería mapear forkee")
    void mapToForkEvent_ok() throws Exception {
        String json = """
        {
          "id": "e2",
          "type": "ForkEvent",
          "actor": { "id": 9, "login": "u", "avatar_url": "", "html_url": "", "type": "User" },
          "repo": { "id": 10, "name": "r", "full_name": "itba/r", "html_url": "h", "description": "", "private": false },
          "created_at": "2025-01-01T00:00:00Z",
          "payload": { "forkee": { "id": 99, "name": "forked", "full_name": "itba/forked", "html_url": "hf", "description": "df", "private": true } }
        }
        """;
        JsonNode node = om.readTree(json);
        ForkEvent ev = mapper.mapToForkEvent(node);
        assertThat(ev.getType()).isEqualTo(EventType.FORK);
        assertThat(ev.getForkee()).isNotNull();
        assertThat(ev.getForkee().name()).isEqualTo("forked");
    }

    @Test
    @DisplayName("mapToCreateEvent debería mapear ref, refType, masterBranch, description")
    void mapToCreateEvent_ok() throws Exception {
        String json = """
        {
          "id": "e3",
          "type": "CreateEvent",
          "actor": { "id": 1, "login": "u", "avatar_url": "", "html_url": "", "type": "User" },
          "repo": { "id": 2, "name": "r", "full_name": "itba/r", "html_url": "h", "description": "", "private": false },
          "created_at": "2025-01-04T00:00:00Z",
          "payload": { "ref": "v1.0.0", "ref_type": "tag", "master_branch": "main", "description": "release" }
        }
        """;
        JsonNode node = om.readTree(json);
        CreateEvent ev = mapper.mapToCreateEvent(node);
        assertThat(ev.getType()).isEqualTo(EventType.CREATE);
        assertThat(ev.getRef()).isEqualTo("v1.0.0");
        assertThat(ev.getRefType()).isEqualTo("tag");
        assertThat(ev.getMasterBranch()).isEqualTo("main");
        assertThat(ev.getDescription()).isEqualTo("release");
    }

    @Test
    @DisplayName("mapToEvents debería mapear PullRequestEvent con action y pullRequest")
    void mapToEvents_prEvent() throws Exception {
        String jsonArray = """
        [
          {
            "id": "pr-evt-1",
            "type": "PullRequestEvent",
            "actor": { "id": 111, "login": "actor", "avatar_url": "", "html_url": "", "type": "User" },
            "repo": { "id": 222, "name": "repo", "full_name": "itba/repo", "html_url":"", "description":"", "private": false },
            "created_at": "2025-02-01T10:00:00Z",
            "payload": {
              "action": "opened",
              "pull_request": {
                "id": 321,
                "number": 5,
                "title": "Add feature X",
                "state": "open",
                "user": { "id": 333, "login":"pruser", "avatar_url":"", "html_url":"", "type":"User" },
                "created_at": "2025-02-01T09:59:00Z",
                "updated_at": "2025-02-01T10:00:00Z",
                "closed_at": null,
                "merged_at": null,
                "html_url": "https://pr/321"
              }
            }
          }
        ]
        """;
        JsonNode root = om.readTree(jsonArray);
        List<Event> events = mapper.mapToEvents(root);

        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(PullRequestEvent.class);
        PullRequestEvent ev = (PullRequestEvent) events.get(0);
        assertThat(ev.getType()).isEqualTo(EventType.PULL_REQUEST);
        assertThat(ev.getAction()).isEqualTo(PullRequestAction.OPENED);
        assertThat(ev.getPullRequest()).isNotNull();
        assertThat(ev.getPullRequest().getId()).isEqualTo(321L);
        assertThat(ev.getPullRequest().getUser().username()).isEqualTo("pruser");
    }

    @Test
    @DisplayName("mapToEvents debería ignorar tipos desconocidos (default -> Optional.empty)")
    void mapToEvents_unknownType_isSkipped() throws Exception {
        String jsonArray = """
        [
          { "id": "ignore", "type": "SomeUnknownEvent", "created_at": "2025-01-01T00:00:00Z" }
        ]
        """;
        JsonNode root = om.readTree(jsonArray);
        List<Event> events = mapper.mapToEvents(root);
        assertThat(events).isEmpty();
    }

    @Test
    @DisplayName("mapToPullRequests (lista) debería mapear múltiples PRs")
    void mapToPullRequests_list() throws Exception {
        String json = """
        [
          {
            "id": 11, "number": 101, "title": "A", "state":"open",
            "user": { "id":1, "login":"u1", "avatar_url":"", "html_url":"", "type":"User" },
            "created_at":"2025-03-01T10:00:00Z", "updated_at":"2025-03-01T10:10:00Z",
            "closed_at": null, "merged_at": null, "html_url":"h1"
          },
          {
            "id": 12, "number": 102, "title": "B", "state":"closed",
            "user": { "id":2, "login":"u2", "avatar_url":"", "html_url":"", "type":"User" },
            "created_at":"2025-03-02T10:00:00Z", "updated_at":"2025-03-02T10:10:00Z",
            "closed_at": "2025-03-02T10:09:00Z", "merged_at": "2025-03-02T10:09:00Z", "html_url":"h2"
          }
        ]
        """;
        JsonNode root = om.readTree(json);
        List<PullRequest> prs = mapper.mapToPullRequests(root);
        assertThat(prs).hasSize(2);
        assertThat(prs.get(0).getNumber()).isEqualTo(101);
        assertThat(prs.get(1).isMerged()).isTrue();
    }

    @Test
    @DisplayName("mapToCommits (lista) debería mapear múltiples commits")
    void mapToCommits_list() throws Exception {
        String json = """
        [
          {
            "sha": "1",
            "commit": {"message":"m1","author":{"name":"A","date":"2025-01-01T00:00:01Z"}},
            "html_url":"u1"
          },
          {
            "sha": "2",
            "commit": {"message":"m2","author":{"name":"B","date":"2025-01-01T00:00:02Z"}},
            "html_url":"u2"
          }
        ]
        """;
        JsonNode root = om.readTree(json);
        List<Commit> commits = mapper.mapToCommits(root);
        assertThat(commits).hasSize(2);
        assertThat(commits.get(0).sha()).isEqualTo("1");
        assertThat(commits.get(1).message()).isEqualTo("m2");
    }

    @Test
    @DisplayName("mapToEvents heterogéneo: Push, Fork y Create")
    void mapToEvents_all() throws Exception {
        String jsonArray = """
        [
          {
            "id": "1",
            "type": "PushEvent",
            "actor": { "id": 1, "login": "a", "avatar_url": "", "html_url": "", "type": "User" },
            "repo": { "id": 1, "name": "r1", "full_name": "itba/r1", "html_url": "h", "description": "", "private": false },
            "created_at": "2025-01-01T00:00:00Z",
            "payload": { "ref":"refs/heads/main", "before":"a", "head":"b", "commits":[] }
          },
          {
            "id": "2",
            "type": "ForkEvent",
            "actor": { "id": 1, "login": "a", "avatar_url": "", "html_url": "", "type": "User" },
            "repo": { "id": 1, "name": "r1", "full_name": "itba/r1", "html_url": "h", "description": "", "private": false },
            "created_at": "2025-01-01T00:00:00Z",
            "payload": { "forkee": { "id": 3, "name":"f", "full_name":"itba/f", "html_url":"hf", "description":"", "private": false } }
          },
          {
            "id": "3",
            "type": "CreateEvent",
            "actor": { "id": 1, "login": "a", "avatar_url": "", "html_url": "", "type": "User" },
            "repo": { "id": 1, "name": "r1", "full_name": "itba/r1", "html_url": "h", "description": "", "private": false },
            "created_at": "2025-01-01T00:00:00Z",
            "payload": { "ref":"x", "ref_type":"branch", "master_branch":"main", "description":"" }
          }
        ]
        """;
        JsonNode root = om.readTree(jsonArray);
        List<Event> events = mapper.mapToEvents(root);
        assertThat(events).hasSize(3);
        assertThat(events.get(0).getType()).isEqualTo(EventType.PUSH);
        assertThat(events.get(1).getType()).isEqualTo(EventType.FORK);
        assertThat(events.get(2).getType()).isEqualTo(EventType.CREATE);
    }

    @Test
    @DisplayName("mapToPullRequest: si closed_at / merged_at NO existen, quedan en null (rama faltante)")
    void mapToPullRequest_missingOptionalDates() throws Exception {
        String json = """
    {
      "id": 44,
      "number": 900,
      "title": "no optional fields",
      "state": "open",
      "user": { "id": 77, "login": "u", "avatar_url": "", "html_url": "", "type": "User" },
      "created_at": "2025-05-01T10:00:00Z",
      "updated_at": "2025-05-01T10:10:00Z",
      "html_url": "https://pr/44"
    }
    """;
        JsonNode node = om.readTree(json);
        PullRequest pr = mapper.mapToPullRequest(node);

        assertThat(pr.getId()).isEqualTo(44L);
        assertThat(pr.getNumber()).isEqualTo(900);
        assertThat(pr.getClosedAt()).isNull();
        assertThat(pr.getMergedAt()).isNull();
        assertThat(pr.isMerged()).isFalse();
    }


}
