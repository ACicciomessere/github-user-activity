package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRepositoryCommitsUseCase {
    private final RepositoryOutboundPort repositoryOutboundPort;

    public List<Commit> execute(String ownerName, String repositoryName, int page, int perPage) {
        return repositoryOutboundPort.getCommits(ownerName, repositoryName, page, perPage);
    }
}

