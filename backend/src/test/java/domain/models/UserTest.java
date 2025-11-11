package domain.models;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testUserRecordConstructionAndAccessors() {
        Long id = 123456L;
        String username = "testuser";
        String avatarUrl = "https://avatars.githubusercontent.com/u/123456";
        String profileUrl = "https://github.com/testuser";
        String type = "User";

        User user = new User(id, username, avatarUrl, profileUrl, type);

        assertThat(user).isNotNull();
        assertThat(user.id()).isEqualTo(id);
        assertThat(user.username()).isEqualTo(username);
        assertThat(user.avatarUrl()).isEqualTo(avatarUrl);
        assertThat(user.profileUrl()).isEqualTo(profileUrl);
        assertThat(user.type()).isEqualTo(type);
    }
}
