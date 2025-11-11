package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetUserEventsByTypeUseCase;
import edu.itba.useractivity.application.usecases.GetUserEventsUseCase;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GetUserEventsByTypeUseCaseTest {

    @Test
    @DisplayName("execute filtra correctamente los eventos según el tipo especificado")
    void execute_filtersCorrectly() {
        GetUserEventsUseCase getUserEventsUseCase = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase useCase = new GetUserEventsByTypeUseCase(getUserEventsUseCase);

        String username = "roni";
        int page = 1, perPage = 10;

        Event e1 = mock(Event.class);
        Event e2 = mock(Event.class);
        Event e3 = mock(Event.class);

        when(e1.getType()).thenReturn(EventType.PUSH);
        when(e2.getType()).thenReturn(EventType.FORK);
        when(e3.getType()).thenReturn(EventType.PUSH);

        when(getUserEventsUseCase.execute(username, page, perPage)).thenReturn(List.of(e1, e2, e3));

        List<Event> result = useCase.execute(EventType.PUSH, username, page, perPage);

        assertThat(result).containsExactly(e1, e3);
        verify(getUserEventsUseCase).execute(eq(username), eq(page), eq(perPage));
        verifyNoMoreInteractions(getUserEventsUseCase);
    }

    @Test
    @DisplayName("execute devuelve lista vacía cuando ningún evento coincide con el tipo")
    void execute_returnsEmptyIfNoMatch() {
        GetUserEventsUseCase getUserEventsUseCase = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase useCase = new GetUserEventsByTypeUseCase(getUserEventsUseCase);

        String username = "roni";
        int page = 2, perPage = 5;

        Event e1 = mock(Event.class);
        Event e2 = mock(Event.class);

        when(e1.getType()).thenReturn(EventType.CREATE);
        when(e2.getType()).thenReturn(EventType.FORK);

        when(getUserEventsUseCase.execute(username, page, perPage)).thenReturn(List.of(e1, e2));

        List<Event> result = useCase.execute(EventType.PULL_REQUEST, username, page, perPage);

        assertThat(result).isEmpty();
        verify(getUserEventsUseCase).execute(eq(username), eq(page), eq(perPage));
        verifyNoMoreInteractions(getUserEventsUseCase);
    }

    @Test
    @DisplayName("execute con lista vacía devuelve vacío (stream sobre vacío)")
    void execute_emptyInput_returnsEmpty() {
        GetUserEventsUseCase getUserEventsUseCase = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase useCase = new GetUserEventsByTypeUseCase(getUserEventsUseCase);

        String username = "u";
        int page = 1, perPage = 10;

        when(getUserEventsUseCase.execute(username, page, perPage)).thenReturn(List.of());

        List<Event> result = useCase.execute(EventType.PUSH, username, page, perPage);

        assertThat(result).isEmpty();
        verify(getUserEventsUseCase).execute(username, page, perPage);
        verifyNoMoreInteractions(getUserEventsUseCase);
    }

    @Test
    @DisplayName("execute con eventType nulo lanza NullPointerException")
    void execute_nullEventType_throws() {
        GetUserEventsUseCase getUserEventsUseCase = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase useCase = new GetUserEventsByTypeUseCase(getUserEventsUseCase);

        when(getUserEventsUseCase.execute(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(mock(Event.class)));

        assertThatThrownBy(() -> useCase.execute(null, "u", 1, 10))
                .isInstanceOf(NullPointerException.class);

        verify(getUserEventsUseCase).execute("u", 1, 10);
        verifyNoMoreInteractions(getUserEventsUseCase);
    }

    @Test
    @DisplayName("execute filtra fuera eventos cuyo getType() es null (no coinciden)")
    void execute_eventWithNullType_isFilteredOut() {
        GetUserEventsUseCase base = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase useCase = new GetUserEventsByTypeUseCase(base);

        Event e1 = mock(Event.class);
        Event e2 = mock(Event.class);

        when(e1.getType()).thenReturn(null);            // <- caso especial
        when(e2.getType()).thenReturn(EventType.FORK);  // tampoco coincide

        when(base.execute("u", 1, 10)).thenReturn(List.of(e1, e2));

        var result = useCase.execute(EventType.PUSH, "u", 1, 10);

        assertThat(result).isEmpty();
        verify(base).execute("u", 1, 10);
        verifyNoMoreInteractions(base);
    }

    @Test
    @DisplayName("execute lanza NullPointerException si el use case base devuelve null")
    void execute_upstreamReturnsNull_throws() {
        GetUserEventsUseCase base = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase useCase = new GetUserEventsByTypeUseCase(base);

        when(base.execute(anyString(), anyInt(), anyInt())).thenReturn(null); // <- null en vez de lista

        assertThatThrownBy(() -> useCase.execute(EventType.PUSH, "u", 1, 10))
                .isInstanceOf(NullPointerException.class); // .stream() sobre null

        verify(base).execute("u", 1, 10);
        verifyNoMoreInteractions(base);
    }
}
