package domain.models;


import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class PullRequestTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();
    private final ZonedDateTime YESTERDAY = NOW.minusDays(1);
    private final User mockUser = new User(1L, "user", "avatar", "profile", "User");

    @Test
    void testPullRequestConstructionAndAccessors() {
        Long id = 1001L;
        Integer number = 5;
        String title = "Feature: Implement caching";
        String state = "open";
        String htmlUrl = "https://github.com/repo/pull/5";

        PullRequest pullRequest = new PullRequest(
                id, number, title, state, mockUser, YESTERDAY, NOW, null, null, htmlUrl
        );

        assertThat(pullRequest).isNotNull();
        assertThat(pullRequest.getId()).isEqualTo(id);
        assertThat(pullRequest.getNumber()).isEqualTo(number);
        assertThat(pullRequest.getTitle()).isEqualTo(title);
        assertThat(pullRequest.getState()).isEqualTo(state);
        assertThat(pullRequest.getUser()).isEqualTo(mockUser);
        assertThat(pullRequest.getCreatedAt()).isEqualTo(YESTERDAY);
        assertThat(pullRequest.getUpdatedAt()).isEqualTo(NOW);
        assertThat(pullRequest.getClosedAt()).isNull();
        assertThat(pullRequest.getMergedAt()).isNull();
        assertThat(pullRequest.getHtmlUrl()).isEqualTo(htmlUrl);
    }

    @Test
    void testIsMerged_whenNotMerged() {
        PullRequest pullRequest = new PullRequest(
                1L, 1, "PR", "closed", mockUser, YESTERDAY, NOW, NOW, null, "url"
        );

        assertThat(pullRequest.isMerged()).isFalse();
    }

    @Test
    void testIsMerged_whenIsMerged() {
        PullRequest pullRequest = new PullRequest(
                1L, 1, "PR", "merged", mockUser, YESTERDAY, NOW, NOW, NOW.plusHours(1), "url"
        );

        assertThat(pullRequest.isMerged()).isTrue();
    }
}
