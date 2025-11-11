package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.*;
@Service
@RequiredArgsConstructor
public class GetRepositoryPullRequestsLifeAvgUseCase {

    private static final int PAGE_SIZE = 100;

    private final RepositoryOutboundPort repositoryOutboundPort;

    public List<PullRequestsLifeAvg> execute(String owner, String repository) {
        List<PullRequest> pullRequests = fetchAllPullRequests(owner, repository);

        List<PullRequest> closedPullRequests = filterClosedPullRequests(pullRequests);
        if (closedPullRequests.isEmpty()) {
            return List.of();
        }

        return computeMonthlyAverages(closedPullRequests);
    }

    private List<PullRequest> fetchAllPullRequests(String owner, String repository) {
        List<PullRequest> allPullRequests = new ArrayList<>();
        int page = 1;
        List<PullRequest> currentPage;

        do {
            currentPage = repositoryOutboundPort.getPullRequests(owner, repository, page, PAGE_SIZE);
            allPullRequests.addAll(currentPage);
            page++;
        } while (currentPage.size() == PAGE_SIZE);

        return allPullRequests;
    }

    private List<PullRequest> filterClosedPullRequests(List<PullRequest> pullRequests) {
        return pullRequests.stream()
                .filter(pr -> pr.getClosedAt() != null)
                .toList();
    }

    private List<PullRequestsLifeAvg> computeMonthlyAverages(List<PullRequest> closedPullRequests) {
        return closedPullRequests.stream()
                .collect(Collectors.groupingBy(pr -> YearMonth.from(pr.getCreatedAt())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(this::mapToPullRequestsLifeAvg)
                .toList();
    }

    private PullRequestsLifeAvg mapToPullRequestsLifeAvg(Map.Entry<YearMonth, List<PullRequest>> entry) {
        YearMonth month = entry.getKey();
        List<PullRequest> prs = entry.getValue();

        Duration totalDuration = prs.stream()
                .map(pr -> Duration.between(pr.getCreatedAt(), pr.getClosedAt()))
                .reduce(Duration.ZERO, Duration::plus);

        Duration avgDuration = prs.isEmpty()
                ? Duration.ZERO
                : totalDuration.dividedBy(prs.size());

        return new PullRequestsLifeAvg(month.toString(), avgDuration, prs.size());
    }

}
