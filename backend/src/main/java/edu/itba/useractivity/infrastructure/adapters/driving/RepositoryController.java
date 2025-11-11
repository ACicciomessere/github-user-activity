package edu.itba.useractivity.infrastructure.adapters.driving;

import edu.itba.useractivity.application.GetRepositoryCommitsUseCase;
import edu.itba.useractivity.application.GetRepositoryMergedPullRequestsUseCase;
import edu.itba.useractivity.application.GetRepositoryPullRequestsUseCase;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repository/{owner}/{repository}")
@RequiredArgsConstructor
public class RepositoryController {
    private final GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase;
    private final GetRepositoryCommitsUseCase getRepositoryCommits;
    private final GetRepositoryMergedPullRequestsUseCase getMergedPullRequestsUseCase;

    private String owner;
    private String repository;

    @ModelAttribute
    public void setRepositoryContext(
            @PathVariable("owner") String owner,
            @PathVariable("repository") String repository
    ) {
        this.owner = owner;
        this.repository = repository;
    }

    @GetMapping("/pull-requests")
    public ResponseEntity<List<PullRequest>> getRepositoryPullRequests(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        List<PullRequest> pullRequests = getRepositoryPullRequestsUseCase.execute(owner, repository, page, perPage);
        return ResponseEntity.ok(pullRequests);
    }

    @GetMapping("/pull-requests/merged")
    public ResponseEntity<List<PullRequest>> getMergedPullRequests(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        List<PullRequest> mergedPRs = getMergedPullRequestsUseCase.execute(owner, repository, page, perPage);
        return ResponseEntity.ok(mergedPRs);
    }

    @GetMapping("/commits")
    public ResponseEntity<List<Commit>> getRepositoryCommits(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        List<Commit> commits = getRepositoryCommits.execute(owner, repository, page, perPage);
        return ResponseEntity.ok(commits);
    }
}
