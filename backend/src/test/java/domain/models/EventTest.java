package domain.models;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.models.Repository;
import edu.itba.useractivity.domain.models.User;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();
    private final User mockUser = new User(1L, "user", "avatar", "profile", "User");
    private final Repository mockRepo = new Repository(1L, "repo", "full/repo", "url", null, false, mockUser);


    @Getter
    @SuperBuilder
    private static class ConcreteEvent extends Event {
    }

    @Test
    void testEventBaseClassBuilderAndAccessors() {
        String id = "12345";
        EventType type = EventType.FORK;

        ConcreteEvent event = ConcreteEvent.builder()
                .id(id)
                .type(type)
                .user(mockUser)
                .repo(mockRepo)
                .createdAt(NOW)
                .build();

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getType()).isEqualTo(type);
        assertThat(event.getUser()).isEqualTo(mockUser);
        assertThat(event.getRepo()).isEqualTo(mockRepo);
        assertThat(event.getCreatedAt()).isEqualTo(NOW);
    }
}
