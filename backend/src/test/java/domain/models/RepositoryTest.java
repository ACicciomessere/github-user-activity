package domain.models;

import edu.itba.useractivity.domain.models.Repository;
import edu.itba.useractivity.domain.models.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RepositoryTest {

    @Test
    void testRepositoryRecordConstructionAndAccessors() {
        // Arrange
        Long id = 789012L;
        String name = "user-activity-service";
        String fullName = "itba/user-activity-service";
        String htmlUrl = "https://github.com/itba/user-activity-service";
        String description = "A service to track GitHub user activity.";
        Boolean isPrivate = false;

        User owner = new User(1L, "owner", "avatar", "profile", "User");

        // Act
        Repository repository = new Repository(id, name, fullName, htmlUrl, description, isPrivate, owner);

        // Assert
        assertThat(repository).isNotNull();
        assertThat(repository.id()).isEqualTo(id);
        assertThat(repository.name()).isEqualTo(name);
        assertThat(repository.fullName()).isEqualTo(fullName);
        assertThat(repository.htmlUrl()).isEqualTo(htmlUrl);
        assertThat(repository.description()).isEqualTo(description);
        assertThat(repository.isPrivate()).isEqualTo(isPrivate);
        assertThat(repository.owner()).isEqualTo(owner);
    }
}