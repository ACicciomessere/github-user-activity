package domain.models;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.enums.PullRequestAction;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.models.PullRequestEvent;
import edu.itba.useractivity.domain.models.Repository;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class PullRequestEventTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();
    private final User mockUser = new User(1L, "user", "avatar", "profile", "User");
    private final Repository mockRepo = new Repository(1L, "repo", "full/repo", "url", null, false, mockUser);
    private final PullRequest mockPR = new PullRequest(10L, 1, "Title", "open", mockUser, NOW, NOW, null, null, "prUrl");

    @Test
    void testPullRequestEventBuilderAndAccessors() {
        String id = "pr1111";
        EventType type = EventType.PULL_REQUEST;
        PullRequestAction action = PullRequestAction.OPENED;

        PullRequestEvent event = PullRequestEvent.builder()
                .id(id)
                .type(type)
                .user(mockUser)
                .repo(mockRepo)
                .createdAt(NOW)
                .action(action)
                .pullRequest(mockPR)
                .build();

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getType()).isEqualTo(type);
        assertThat(event.getAction()).isEqualTo(action);
        assertThat(event.getPullRequest()).isEqualTo(mockPR);
    }
}
