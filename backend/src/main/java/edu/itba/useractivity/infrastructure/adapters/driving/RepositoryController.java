package edu.itba.useractivity.infrastructure.adapters.driving;

import edu.itba.useractivity.application.GetRepositoryPullRequestsUseCase;
import edu.itba.useractivity.domain.models.PullRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/repository")
@AllArgsConstructor
public class RepositoryController {
    private final GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase;

    @GetMapping("/{repository}/owner/{owner}/pull-requests")
    public ResponseEntity<List<PullRequest>> getEvents(
            @PathVariable("repository") String repository,
            @PathVariable("owner") String owner
    ) {
        List<PullRequest> pullRequests = getRepositoryPullRequestsUseCase.execute(owner, repository);
        return ResponseEntity.ok(pullRequests);
    }
}
