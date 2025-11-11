package useractivity.applicaction;

import edu.itba.useractivity.App;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class AppTest {

    @Test
    @DisplayName("main invoca SpringApplication.run(App.class, args) correctamente")
    void main_invokesSpringRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            String[] args = {"--test"};
            App.main(args);
            mocked.verify(() -> SpringApplication.run(eq(App.class), eq(args)));
        }
    }
}
