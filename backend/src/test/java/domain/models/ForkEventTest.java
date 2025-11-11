package domain.models;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.ForkEvent;
import edu.itba.useractivity.domain.models.Repository;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class ForkEventTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();
    private final User mockUser = new User(1L, "user", "avatar", "profile", "User");
    private final Repository mockRepo = new Repository(1L, "repo", "full/repo", "url", null, false, mockUser);
    private final Repository mockForkee = new Repository(2L, "forkee-repo", "user/forkee-repo", "url", "Forked!", true, mockUser);

    @Test
    void testForkEventBuilderAndAccessors() {
        String id = "f98765";
        EventType type = EventType.FORK;

        ForkEvent event = ForkEvent.builder()
                .id(id)
                .type(type)
                .user(mockUser)
                .repo(mockRepo)
                .createdAt(NOW)
                .forkee(mockForkee)
                .build();

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getType()).isEqualTo(type);
        assertThat(event.getForkee()).isEqualTo(mockForkee);
    }
}
