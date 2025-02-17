package co.com.franchise.api;

import co.com.franchise.api.dto.request.BranchRequestDto;
import co.com.franchise.api.mapper.BranchMapper;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class BranchHandler {
    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BranchRequestDto.class)
                .flatMap(branchRequestDto -> {
                    Branch branch = BranchMapper.MAPPER.requestDtoToBranch(branchRequestDto);
                    return branchUseCase.save(branch)
                            .flatMap(branchResponse -> ServerResponse
                                    .created(URI.create("/api/v1/branches/" + branchResponse.getId()))
                                    .bodyValue(BranchMapper.MAPPER.branchToResponseDto(branchResponse)));
                });
    }
}
