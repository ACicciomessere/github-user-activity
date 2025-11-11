package domain.models;

import edu.itba.useractivity.domain.models.Commit;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class CommitTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();

    @Test
    void testCommitRecordConstructionAndAccessors() {

        String sha = "a1b2c3d4e5f6";
        String message = "Fix: Bug in API response handling";
        String authorName = "Test Author";
        String htmlUrl = "https://github.com/repo/commit/sha";

        Commit commit = new Commit(sha, message, authorName, NOW, htmlUrl);

        assertThat(commit).isNotNull();
        assertThat(commit.sha()).isEqualTo(sha);
        assertThat(commit.message()).isEqualTo(message);
        assertThat(commit.authorName()).isEqualTo(authorName);
        assertThat(commit.date()).isEqualTo(NOW);
        assertThat(commit.htmlUrl()).isEqualTo(htmlUrl);
    }
}