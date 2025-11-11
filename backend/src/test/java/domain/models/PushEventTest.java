package domain.models;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PushEvent;
import edu.itba.useractivity.domain.models.Repository;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class PushEventTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();
    private final User mockUser = new User(1L, "user", "avatar", "profile", "User");
    private final Repository mockRepo = new Repository(1L, "repo", "full/repo", "url", null, false, mockUser);
    private final Commit mockCommit1 = new Commit("c1", "message1", "author1", NOW, "url1");
    private final Commit mockCommit2 = new Commit("c2", "message2", "author2", NOW, "url2");
    private final List<Commit> mockCommits = Arrays.asList(mockCommit1, mockCommit2);



    @Test
    void testPushEventBuilderAndAccessors() {
        String id = "p7777";
        EventType type = EventType.PUSH;
        String ref = "refs/heads/main";
        String before = "old-sha";
        String head = "new-sha";

        PushEvent event = PushEvent.builder()
                .id(id)
                .type(type)
                .user(mockUser)
                .repo(mockRepo)
                .createdAt(NOW)
                .ref(ref)
                .before(before)
                .head(head)
                .commits(mockCommits)
                .build();

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getType()).isEqualTo(type);
        assertThat(event.getRef()).isEqualTo(ref);
        assertThat(event.getBefore()).isEqualTo(before);
        assertThat(event.getHead()).isEqualTo(head);
        assertThat(event.getCommits()).isEqualTo(mockCommits);
    }
}
