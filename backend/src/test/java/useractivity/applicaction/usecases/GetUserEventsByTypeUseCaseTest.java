package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetUserEventsByTypeUseCase;
import edu.itba.useractivity.application.usecases.GetUserEventsUseCase;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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

        List<Event> events = List.of(e1, e2, e3);
        when(getUserEventsUseCase.execute(username, page, perPage)).thenReturn(events);

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
}
