package co.com.franchise.api;

import co.com.franchise.api.dto.request.FranchiseRequestDto;
import co.com.franchise.api.mapper.FranchiseMapper;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;
    public Mono<ServerResponse>  getAll(ServerRequest serverRequest) {
        return parsePagination(serverRequest)
                .flatMap(tuple -> franchiseUseCase.getAll(tuple.getT1(), tuple.getT2())
                        .flatMap(franchises -> ServerResponse.ok().bodyValue(FranchiseMapper.MAPPER
                                .listFranchisesToResponseDto(franchises))));
    }

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FranchiseRequestDto.class)
                .flatMap(franchiseRequestDto -> {
                    Franchise franchise = Franchise.builder()
                            .name(franchiseRequestDto.getName())
                            .build();
                    return franchiseUseCase.save(franchise)
                            .flatMap(franchiseResponse -> ServerResponse
                                    .created(URI.create("/api/v1/franchises/" + franchiseResponse.getId()))
                                    .bodyValue(FranchiseMapper.MAPPER.franchiseToResponseDto(franchiseResponse)));
                });
    }

    private Mono<Tuple2<Integer, Integer>> parsePagination(ServerRequest request) {
        return Mono.fromCallable(() -> {
            int page = request.queryParam("page")
                    .map(Integer::parseInt)
                    .orElse(0);
            int size = request.queryParam("size")
                    .map(Integer::parseInt)
                    .orElse(10);
            return Tuples.of(page, size);
        });
    }
}
