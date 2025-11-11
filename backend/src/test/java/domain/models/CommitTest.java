package domain.models;

import edu.itba.useractivity.domain.models.Commit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommitTest {

    private final ZonedDateTime NOW = ZonedDateTime.parse("2025-01-01T00:00:00Z");

    @Test
    @DisplayName("constructor y accessors devuelven los valores asignados")
    void constructor_and_accessors() {
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

    @Test
    @DisplayName("equals/hashCode: dos commits iguales son iguales y tienen mismo hash")
    void equals_and_hashCode_sameValues() {
        Commit c1 = new Commit("abc", "msg", "alice", NOW, "u1");
        Commit c2 = new Commit("abc", "msg", "alice", NOW, "u1");

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    @DisplayName("equals/hashCode: commits distintos no son iguales y hash distinto")
    void equals_and_hashCode_differentValues() {
        Commit base = new Commit("abc", "msg", "alice", NOW, "u1");
        Commit differentSha = new Commit("XYZ", "msg", "alice", NOW, "u1");
        Commit differentMessage = new Commit("abc", "MSG2", "alice", NOW, "u1");
        Commit differentAuthor = new Commit("abc", "msg", "bob", NOW, "u1");
        Commit differentDate = new Commit("abc", "msg", "alice", NOW.plusDays(1), "u1");
        Commit differentUrl = new Commit("abc", "msg", "alice", NOW, "u2");

        assertThat(base).isNotEqualTo(differentSha);
        assertThat(base).isNotEqualTo(differentMessage);
        assertThat(base).isNotEqualTo(differentAuthor);
        assertThat(base).isNotEqualTo(differentDate);
        assertThat(base).isNotEqualTo(differentUrl);

        // sanity: al menos algunos hashes deberían diferir
        assertThat(base.hashCode()).isNotEqualTo(differentSha.hashCode());
    }

    @Test
    @DisplayName("equals: no es igual a null ni a otro tipo")
    void equals_null_and_otherType() {
        Commit c = new Commit("abc", "msg", "alice", NOW, "u1");

        assertThat(c).isNotEqualTo(null);
        assertThat(c).isNotEqualTo("no soy un commit");
        assertThat(c).isEqualTo(c); // reflexividad
    }

    @Test
    @DisplayName("toString incluye los nombres de los componentes y sus valores")
    void toString_containsFields() {
        Commit c = new Commit("abc", "msg", "alice", NOW, "u1");
        String s = c.toString();

        assertThat(s).contains("Commit");
        assertThat(s).contains("sha=abc");
        assertThat(s).contains("message=msg");
        assertThat(s).contains("authorName=alice");
        assertThat(s).contains("htmlUrl=u1");
    }

    @Test
    @DisplayName("permite null en componentes (si el diseño lo tolera)")
    void allows_null_components() {
        Commit c = new Commit(null, null, null, null, null);
        assertThat(c.sha()).isNull();
        assertThat(c.message()).isNull();
        assertThat(c.authorName()).isNull();
        assertThat(c.date()).isNull();
        assertThat(c.htmlUrl()).isNull();

        // toString no debe explotar con nulls
        assertThat(c.toString()).contains("sha=null", "message=null", "authorName=null", "date=null", "htmlUrl=null");
    }
}
