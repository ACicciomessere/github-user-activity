package domain.enums;

import edu.itba.useractivity.domain.enums.PullRequestAction;
import edu.itba.useractivity.domain.exceptions.InvalidPullRequestActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PullRequestActionTest {

    @Test
    @DisplayName("Cada constante tiene el valor esperado")
    void eachEnumHasExpectedValue() {
        assertThat(PullRequestAction.OPENED.getValue()).isEqualTo("opened");
        assertThat(PullRequestAction.CLOSED.getValue()).isEqualTo("closed");
        assertThat(PullRequestAction.REOPENED.getValue()).isEqualTo("reopened");
        assertThat(PullRequestAction.EDITED.getValue()).isEqualTo("edited");
        assertThat(PullRequestAction.ASSIGNED.getValue()).isEqualTo("assigned");
        assertThat(PullRequestAction.UNASSIGNED.getValue()).isEqualTo("unassigned");
        assertThat(PullRequestAction.LABELED.getValue()).isEqualTo("labeled");
        assertThat(PullRequestAction.UNLABELED.getValue()).isEqualTo("unlabeled");
        assertThat(PullRequestAction.SYNCHRONIZE.getValue()).isEqualTo("synchronize");
        assertThat(PullRequestAction.READY_FOR_REVIEW.getValue()).isEqualTo("ready_for_review");
        assertThat(PullRequestAction.CONVERTED_TO_DRAFT.getValue()).isEqualTo("converted_to_draft");
        assertThat(PullRequestAction.LOCKED.getValue()).isEqualTo("locked");
        assertThat(PullRequestAction.UNLOCKED.getValue()).isEqualTo("unlocked");
        assertThat(PullRequestAction.REVIEW_REQUESTED.getValue()).isEqualTo("review_requested");
        assertThat(PullRequestAction.REVIEW_REQUEST_REMOVED.getValue()).isEqualTo("review_request_removed");
        assertThat(PullRequestAction.MERGED.getValue()).isEqualTo("merged");
    }

    @Test
    @DisplayName("fromString devuelve el valor correcto cuando la cadena es válida (case insensitive)")
    void fromString_validValues_returnEnum() {
        assertThat(PullRequestAction.fromString("opened")).isEqualTo(PullRequestAction.OPENED);
        assertThat(PullRequestAction.fromString("CLOSED")).isEqualTo(PullRequestAction.CLOSED);
        assertThat(PullRequestAction.fromString("ReOpEnEd")).isEqualTo(PullRequestAction.REOPENED);
        assertThat(PullRequestAction.fromString("ready_for_review")).isEqualTo(PullRequestAction.READY_FOR_REVIEW);
        assertThat(PullRequestAction.fromString("MERGED")).isEqualTo(PullRequestAction.MERGED);
    }

    @Test
    @DisplayName("fromString lanza InvalidPullRequestActionException cuando es null o vacío")
    void fromString_nullOrBlank_throws() {
        assertThatThrownBy(() -> PullRequestAction.fromString(null))
                .isInstanceOf(InvalidPullRequestActionException.class)
                .hasMessageContaining("null");
        assertThatThrownBy(() -> PullRequestAction.fromString(""))
                .isInstanceOf(InvalidPullRequestActionException.class);
        assertThatThrownBy(() -> PullRequestAction.fromString("   "))
                .isInstanceOf(InvalidPullRequestActionException.class);
    }

    @Test
    @DisplayName("fromString lanza InvalidPullRequestActionException cuando no hay coincidencia")
    void fromString_unknown_throws() {
        assertThatThrownBy(() -> PullRequestAction.fromString("invalid_action"))
                .isInstanceOf(InvalidPullRequestActionException.class)
                .hasMessageContaining("invalid_action");
    }

    @Test
    @DisplayName("values() y valueOf() funcionan correctamente")
    void valuesAndValueOf_workCorrectly() {
        assertThat(PullRequestAction.valueOf("OPENED")).isEqualTo(PullRequestAction.OPENED);
        assertThat(PullRequestAction.values()).contains(
                PullRequestAction.OPENED,
                PullRequestAction.CLOSED,
                PullRequestAction.MERGED
        );
    }
}
