package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetRepositoryPullRequestsLifeAvgUseCase;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetRepositoryPullRequestsLifeAvgUseCaseTest {

    private static PullRequest pr(ZonedDateTime created, ZonedDateTime closed) {
        PullRequest mock = mock(PullRequest.class);
        when(mock.getCreatedAt()).thenReturn(created);
        when(mock.getClosedAt()).thenReturn(closed);
        return mock;
    }

    @Test
    @DisplayName("Si no hay PRs cerrados, devuelve lista vacía (cubre rama closedPullRequests.isEmpty())")
    void returnsEmptyWhenNoClosedPRs() {
        RepositoryOutboundPort repo = mock(RepositoryOutboundPort.class);
        GetRepositoryPullRequestsLifeAvgUseCase useCase = new GetRepositoryPullRequestsLifeAvgUseCase(repo);

        String owner = "itba", repoName = "user-activity";

        // Página 1: 2 PRs sin cerrar
        ZonedDateTime t = ZonedDateTime.parse("2025-01-01T10:00:00Z");
        List<PullRequest> page1 = List.of(
                pr(t, null),
                pr(t.plusDays(1), null)
        );

        when(repo.getPullRequests(owner, repoName, 1, 100)).thenReturn(page1);

        var result = useCase.execute(owner, repoName);

        assertThat(result).isEmpty();

        // Solo se llama una vez porque el tamaño de la primera página (2) != PAGE_SIZE (100)
        verify(repo).getPullRequests(owner, repoName, 1, 100);
        verifyNoMoreInteractions(repo);
    }

    @Test
    @DisplayName("Pagina correctamente y calcula promedios mensuales (cubre loop y agrupamiento)")
    void paginatesAndComputesMonthlyAverages() {
        RepositoryOutboundPort repo = mock(RepositoryOutboundPort.class);
        GetRepositoryPullRequestsLifeAvgUseCase useCase = new GetRepositoryPullRequestsLifeAvgUseCase(repo);

        String owner = "org", repoName = "project";

        // Primera página con tamaño EXACTO 100 (PAGE_SIZE) -> fuerza segunda iteración
        ZonedDateTime jan1 = ZonedDateTime.parse("2025-01-10T00:00:00Z");
        PullRequest janClosed24h = pr(jan1, jan1.plusHours(24)); // 24h
        List<PullRequest> page1 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            page1.add(janClosed24h);
        }

        // Segunda página con menos de 100 -> corta el bucle
        ZonedDateTime feb1 = ZonedDateTime.parse("2025-02-01T12:00:00Z");
        PullRequest febClosed10h = pr(feb1, feb1.plusHours(10));
        PullRequest febOpen = pr(feb1.plusDays(1), null);
        List<PullRequest> page2 = List.of(febClosed10h, febOpen);

        when(repo.getPullRequests(owner, repoName, 1, 100)).thenReturn(page1);
        when(repo.getPullRequests(owner, repoName, 2, 100)).thenReturn(page2);

        var result = useCase.execute(owner, repoName);

        assertThat(result).hasSize(2);
        PullRequestsLifeAvg janAvg = result.get(0);
        PullRequestsLifeAvg febAvg = result.get(1);

        assertThat(janAvg.month()).isEqualTo(YearMonth.of(2025, 1).toString());
        assertThat(janAvg.count()).isEqualTo(100);
        assertThat(janAvg.hours()).isEqualTo(Duration.ofHours(24));

        assertThat(febAvg.month()).isEqualTo(YearMonth.of(2025, 2).toString());
        assertThat(febAvg.count()).isEqualTo(1);
        assertThat(febAvg.hours()).isEqualTo(Duration.ofHours(10));

        verify(repo).getPullRequests(owner, repoName, 1, 100);
        verify(repo).getPullRequests(owner, repoName, 2, 100);
        verifyNoMoreInteractions(repo);
    }

    @Test
    @DisplayName("mapToPullRequestsLifeAvg con lista vacía devuelve Duration.ZERO (cubre rama ternaria prs.isEmpty())")
    void privateMapToPullRequestsLifeAvg_emptyListBranch() throws Exception {
        RepositoryOutboundPort repo = mock(RepositoryOutboundPort.class);
        GetRepositoryPullRequestsLifeAvgUseCase useCase = new GetRepositoryPullRequestsLifeAvgUseCase(repo);

        Entry<YearMonth, List<PullRequest>> entry =
                Map.entry(YearMonth.of(2025, 3), List.of());

        Method m = GetRepositoryPullRequestsLifeAvgUseCase.class
                .getDeclaredMethod("mapToPullRequestsLifeAvg", Entry.class);
        m.setAccessible(true);

        PullRequestsLifeAvg avg = (PullRequestsLifeAvg) m.invoke(useCase, entry);

        assertThat(avg.month()).isEqualTo("2025-03");
        assertThat(avg.count()).isEqualTo(0L);
        assertThat(avg.hours()).isEqualTo(Duration.ZERO);

        verifyNoInteractions(repo);
    }
}
