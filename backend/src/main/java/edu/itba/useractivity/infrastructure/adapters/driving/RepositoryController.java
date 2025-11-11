package edu.itba.useractivity.infrastructure.adapters.driving;

import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import edu.itba.useractivity.domain.models.CommitsResponse;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.inbound.RepositoryInboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repository/{owner}/{repository}")
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryInboundPort repositoryInboundPort;

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
        List<PullRequest> pullRequests = repositoryInboundPort.getPullRequests(owner, repository, page, perPage);
        return ResponseEntity.ok(pullRequests);
    }

    @GetMapping("/pull-requests/merged")
    public ResponseEntity<List<PullRequest>> getMergedPullRequests(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        List<PullRequest> mergedPRs = repositoryInboundPort.getMergedPullRequests(owner, repository, page, perPage);
        return ResponseEntity.ok(mergedPRs);
    }

    @GetMapping("/commits")
    public ResponseEntity<CommitsResponse> getRepositoryCommits(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        CommitsResponse commitsResponse = repositoryInboundPort.getCommits(owner, repository, page, perPage);
        return ResponseEntity.ok(commitsResponse);
    }

    @GetMapping("/pull-requests/life-avg")
    public ResponseEntity<List<PullRequestsLifeAvg>> getPullRequestsLifeAvg() {
        List<PullRequestsLifeAvg> pullRequestsLifeAvg = repositoryInboundPort.getPullRequestsLifeAvg(owner, repository);
        return ResponseEntity.ok(pullRequestsLifeAvg);
    }
}
