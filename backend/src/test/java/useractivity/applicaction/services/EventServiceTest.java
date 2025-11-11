package useractivity.applicaction.services;

import edu.itba.useractivity.application.services.EventService;
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

class EventServiceTest {

    @Test
    @DisplayName("getUserEvents delega en GetUserEventsUseCase y devuelve su resultado")
    void getUserEvents_delegates() {
        // mocks
        GetUserEventsUseCase getUserEvents = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase getByType = mock(GetUserEventsByTypeUseCase.class);
        EventService service = new EventService(getUserEvents, getByType);

        String username = "alice";
        int page = 2, perPage = 5;
        Event e1 = mock(Event.class), e2 = mock(Event.class);
        List<Event> expected = List.of(e1, e2);

        when(getUserEvents.execute(username, page, perPage)).thenReturn(expected);

        // when
        List<Event> result = service.getUserEvents(username, page, perPage);

        // then
        assertThat(result).isSameAs(expected);
        verify(getUserEvents).execute(eq(username), eq(page), eq(perPage));
        verifyNoMoreInteractions(getUserEvents, getByType);
    }

    @Test
    @DisplayName("getUserEventsByType delega en GetUserEventsByTypeUseCase y devuelve su resultado")
    void getUserEventsByType_delegates() {
        // mocks
        GetUserEventsUseCase getUserEvents = mock(GetUserEventsUseCase.class);
        GetUserEventsByTypeUseCase getByType = mock(GetUserEventsByTypeUseCase.class);
        EventService service = new EventService(getUserEvents, getByType);

        EventType type = EventType.PUSH; // ajustá si tu enum difiere
        String username = "bob";
        int page = 1, perPage = 30;
        Event e = mock(Event.class);
        List<Event> expected = List.of(e);

        when(getByType.execute(type, username, page, perPage)).thenReturn(expected);

        // when
        List<Event> result = service.getUserEventsByType(type, username, page, perPage);

        // then
        assertThat(result).isSameAs(expected);
        verify(getByType).execute(eq(type), eq(username), eq(page), eq(perPage));
        verifyNoMoreInteractions(getUserEvents, getByType);
    }
}
