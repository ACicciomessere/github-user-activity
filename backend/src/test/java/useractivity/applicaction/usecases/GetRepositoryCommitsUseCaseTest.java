package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetRepositoryCommitsUseCase;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.CommitsResponse;
import edu.itba.useractivity.domain.models.UserParticipation;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GetRepositoryCommitsUseCaseTest {

    @Test
    @DisplayName("execute devuelve CommitsResponse con commits de la página y estadísticas de participación")
    void execute_returnsCommitsResponseWithParticipation() {
        // mocks
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryCommitsUseCase useCase = new GetRepositoryCommitsUseCase(repoPort);

        String owner = "itba";
        String repository = "user-activity";
        int page = 2;
        int perPage = 10;


        Commit c1 = new Commit("sha1", "message1", "user1", ZonedDateTime.now(), "url1");
        Commit c2 = new Commit("sha2", "message2", "user2", ZonedDateTime.now(), "url2");
        List<Commit> pageCommits = List.of(c1, c2);

        Commit c3 = new Commit("sha3", "message3", "user1", ZonedDateTime.now(), "url3");
        Commit c4 = new Commit("sha4", "message4", "user1", ZonedDateTime.now(), "url4");
        Commit c5 = new Commit("sha5", "message5", "user2", ZonedDateTime.now(), "url5");
        List<Commit> allCommits = List.of(c1, c2, c3, c4, c5);

        when(repoPort.getCommits(eq(owner), eq(repository), eq(page), eq(perPage))).thenReturn(pageCommits);
        // Mock: primera llamada para obtener todos los commits (página 1 con PAGE_SIZE=100)
        // Como allCommits tiene 5 elementos (< 100), no se llamará a la página 2
        when(repoPort.getCommits(eq(owner), eq(repository), eq(1), eq(100))).thenReturn(allCommits);

        // when
        CommitsResponse result = useCase.execute(owner, repository, page, perPage);

        // then
        assertThat(result).isNotNull();
        assertThat(result.commits()).isEqualTo(pageCommits);
        assertThat(result.userParticipations()).hasSize(2);
        
        // Verificar estadísticas: user1 tiene 3 commits (60%), user2 tiene 2 commits (40%)
        UserParticipation user1Participation = result.userParticipations().get(0);
        assertThat(user1Participation.username()).isEqualTo("user1");
        assertThat(user1Participation.commitCount()).isEqualTo(3);
        assertThat(user1Participation.percentage()).isEqualTo(60.0);
        
        UserParticipation user2Participation = result.userParticipations().get(1);
        assertThat(user2Participation.username()).isEqualTo("user2");
        assertThat(user2Participation.commitCount()).isEqualTo(2);
        assertThat(user2Participation.percentage()).isEqualTo(40.0);
        
        // Verificar que se llamó para obtener los commits de la página solicitada
        verify(repoPort).getCommits(owner, repository, page, perPage);
        // Verificar que se llamó para obtener todos los commits (paginación)
        // Como allCommits tiene 5 elementos (< 100), solo se llama a la página 1
        verify(repoPort).getCommits(owner, repository, 1, 100);
        verify(repoPort, never()).getCommits(owner, repository, 2, 100);
    }

    @Test
    @DisplayName("execute devuelve CommitsResponse vacío cuando no hay commits")
    void execute_emptyCommits() {
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryCommitsUseCase useCase = new GetRepositoryCommitsUseCase(repoPort);

        String owner = "a";
        String repository = "b";
        int page = 1;
        int perPage = 10;

        // Mock: no hay commits
        when(repoPort.getCommits(eq(owner), eq(repository), eq(1), eq(100))).thenReturn(Collections.emptyList());
        when(repoPort.getCommits(eq(owner), eq(repository), eq(page), eq(perPage))).thenReturn(Collections.emptyList());

        // when
        CommitsResponse result = useCase.execute(owner, repository, page, perPage);

        // then
        assertThat(result).isNotNull();
        assertThat(result.commits()).isEmpty();
        assertThat(result.userParticipations()).isEmpty();
        
        verify(repoPort).getCommits(owner, repository, 1, 100);
        verify(repoPort).getCommits(owner, repository, page, perPage);
    }

    @Test
    @DisplayName("execute calcula correctamente porcentajes con un solo usuario")
    void execute_singleUserParticipation() {
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryCommitsUseCase useCase = new GetRepositoryCommitsUseCase(repoPort);

        String owner = "owner";
        String repository = "repo";
        
        Commit c1 = new Commit("sha1", "msg1", "user1", ZonedDateTime.now(), "url1");
        Commit c2 = new Commit("sha2", "msg2", "user1", ZonedDateTime.now(), "url2");
        Commit c3 = new Commit("sha3", "msg3", "user1", ZonedDateTime.now(), "url3");
        List<Commit> allCommits = List.of(c1, c2, c3);
        List<Commit> pageCommits = List.of(c1);

        when(repoPort.getCommits(eq(owner), eq(repository), eq(1), eq(100))).thenReturn(allCommits);
        when(repoPort.getCommits(eq(owner), eq(repository), eq(2), eq(100))).thenReturn(Collections.emptyList());
        when(repoPort.getCommits(eq(owner), eq(repository), eq(1), eq(10))).thenReturn(pageCommits);

        CommitsResponse result = useCase.execute(owner, repository, 1, 10);

        assertThat(result.userParticipations()).hasSize(1);
        UserParticipation participation = result.userParticipations().get(0);
        assertThat(participation.username()).isEqualTo("user1");
        assertThat(participation.commitCount()).isEqualTo(3);
        assertThat(participation.percentage()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("execute maneja correctamente paginación cuando hay exactamente 100 commits")
    void execute_exactly100Commits() {
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryCommitsUseCase useCase = new GetRepositoryCommitsUseCase(repoPort);

        String owner = "owner";
        String repository = "repo";
        
        // Crear exactamente 100 commits
        List<Commit> firstPage = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            // Distribuir entre user1, user2, user3
            String username = "user" + ((i % 3) + 1);
            firstPage.add(new Commit("sha" + i, "msg" + i, username, ZonedDateTime.now(), "url" + i));
        }
        
        // Segunda página vacía (termina el loop)
        List<Commit> secondPage = Collections.emptyList();
        
        // Commits de la página solicitada
        List<Commit> pageCommits = List.of(firstPage.get(0), firstPage.get(1));

        when(repoPort.getCommits(eq(owner), eq(repository), eq(1), eq(100))).thenReturn(firstPage);
        when(repoPort.getCommits(eq(owner), eq(repository), eq(2), eq(100))).thenReturn(secondPage);
        when(repoPort.getCommits(eq(owner), eq(repository), eq(1), eq(10))).thenReturn(pageCommits);

        CommitsResponse result = useCase.execute(owner, repository, 1, 10);

        assertThat(result.commits()).isEqualTo(pageCommits);
        assertThat(result.userParticipations()).hasSize(3); // user1, user2, user3
        
        // Verificar que se llamó a ambas páginas
        verify(repoPort).getCommits(owner, repository, 1, 100);
        verify(repoPort).getCommits(owner, repository, 2, 100);
    }

}
