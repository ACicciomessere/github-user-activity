package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.CommitsResponse;
import edu.itba.useractivity.domain.models.UserParticipation;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRepositoryCommitsUseCase {
    private static final int PAGE_SIZE = 100;
    
    private final RepositoryOutboundPort repositoryOutboundPort;

    public CommitsResponse execute(String ownerName, String repositoryName, int page, int perPage) {
        List<Commit> commits = repositoryOutboundPort.getCommits(ownerName, repositoryName, page, perPage);

        List<Commit> allCommits = fetchAllCommits(ownerName, repositoryName);

        List<UserParticipation> userParticipations = calculateUserParticipations(allCommits);
        
        return new CommitsResponse(commits, userParticipations);
    }
    
    private List<Commit> fetchAllCommits(String ownerName, String repositoryName) {
        List<Commit> allCommits = new java.util.ArrayList<>();
        int page = 1;
        List<Commit> currentPage;
        
        do {
            currentPage = repositoryOutboundPort.getCommits(ownerName, repositoryName, page, PAGE_SIZE);
            allCommits.addAll(currentPage);
            page++;
        } while (currentPage.size() == PAGE_SIZE);
        
        return allCommits;
    }
    
    private List<UserParticipation> calculateUserParticipations(List<Commit> allCommits) {
        if (allCommits.isEmpty()) {
            return List.of();
        }
        
        long totalCommits = allCommits.size();

        Map<String, Long> commitsByUser = allCommits.stream()
                .collect(Collectors.groupingBy(
                        Commit::authorName,
                        Collectors.counting()
                ));

        return commitsByUser.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    long commitCount = entry.getValue();
                    double percentage = (commitCount * 100.0) / totalCommits;
                    return new UserParticipation(username, commitCount, percentage);
                })
                .sorted((a, b) -> Long.compare(b.commitCount(), a.commitCount())) // Ordenar por cantidad descendente
                .collect(Collectors.toList());
    }
}

