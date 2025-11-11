package domain.models;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.CreateEvent;
import edu.itba.useractivity.domain.models.Repository;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class CreateEventTest {

    private final ZonedDateTime NOW = ZonedDateTime.now();
    private final User mockUser = new User(1L, "user", "avatar", "profile", "User");
    private final Repository mockRepo = new Repository(1L, "repo", "full/repo", "url", null, false, mockUser);


    @Test
    void testCreateEventBuilderAndAccessors() {
        String id = "c54321";
        EventType type = EventType.CREATE;
        String ref = "main";
        String refType = "branch";
        String masterBranch = "main";
        String description = "Initial repository setup";

        CreateEvent event = CreateEvent.builder()
                .id(id)
                .type(type)
                .user(mockUser)
                .repo(mockRepo)
                .createdAt(NOW)
                .ref(ref)
                .refType(refType)
                .masterBranch(masterBranch)
                .description(description)
                .build();

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getType()).isEqualTo(type);
        assertThat(event.getRef()).isEqualTo(ref);
        assertThat(event.getRefType()).isEqualTo(refType);
        assertThat(event.getMasterBranch()).isEqualTo(masterBranch);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}