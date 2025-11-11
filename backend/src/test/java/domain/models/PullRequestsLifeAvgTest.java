package domain.models;

import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class PullRequestsLifeAvgTest {

    @Test
    @DisplayName("Constructor y accesores funcionan correctamente")
    void recordConstructionAndAccessors() {
        String month = "2025-10";
        Duration duration = Duration.ofHours(42);
        long count = 7L;

        PullRequestsLifeAvg avg = new PullRequestsLifeAvg(month, duration, count);

        assertThat(avg.month()).isEqualTo(month);
        assertThat(avg.hours()).isEqualTo(duration);
        assertThat(avg.count()).isEqualTo(count);
    }

    @Test
    @DisplayName("equals, hashCode y toString funcionan como record estándar")
    void equalsHashCodeToString() {
        PullRequestsLifeAvg a = new PullRequestsLifeAvg("2025-10", Duration.ofHours(5), 3L);
        PullRequestsLifeAvg b = new PullRequestsLifeAvg("2025-10", Duration.ofHours(5), 3L);
        PullRequestsLifeAvg c = new PullRequestsLifeAvg("2025-11", Duration.ofHours(7), 4L);

        // equals/hashCode
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);

        // toString contiene los campos
        String str = a.toString();
        assertThat(str).contains("2025-10", "PT5H", "3");
    }
}
