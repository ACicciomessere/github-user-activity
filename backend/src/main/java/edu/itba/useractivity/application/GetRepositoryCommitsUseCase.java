package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.ports.outbound.RepositoryDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRepositoryCommitsUseCase {
    private final RepositoryDataPort repositoryDataPort;

    public List<Commit> execute(String ownerName, String repositoryName) {
        return repositoryDataPort.getCommits(ownerName, repositoryName);
    }
}

