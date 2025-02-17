package co.com.franchise.api;

import co.com.franchise.api.dto.request.AdjustmentStockRequestDto;
import co.com.franchise.api.dto.request.ProductBranchAssociationDTO;
import co.com.franchise.api.mapper.ProductBranchMapper;
import co.com.franchise.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
@RequiredArgsConstructor
public class ProductBranchHandler {

    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> associateProductFromBranch(ServerRequest request) {
        return request.bodyToMono(ProductBranchAssociationDTO.class)
                .flatMap(productBranchAssociationDTO -> parsePathVariables(request)
                        .flatMap(tuple -> productUseCase
                                .associateProductBranch(tuple.getT2(), tuple.getT1(), productBranchAssociationDTO.getStock())
                                .flatMap(productBranch -> ServerResponse.ok().bodyValue(productBranch))));
    }

    public Mono<ServerResponse> disassociateProductFromBranch(ServerRequest request) {
        return parsePathVariables(request)
                .flatMap(tuple -> productUseCase.disassociateProductBranch(tuple.getT2(), tuple.getT1()))
                .then(Mono.defer(() -> ServerResponse.noContent().build()));
    }
    public Mono<ServerResponse> adjustStock(ServerRequest request) {
        return request.bodyToMono(AdjustmentStockRequestDto.class)
                .flatMap(adjustmentStockRequestDto -> parsePathVariables(request)
                            .flatMap(tuple -> productUseCase
                                    .adjustStock(tuple.getT2(), tuple.getT1(), adjustmentStockRequestDto.getQuantity())
                                    .flatMap(productBranch -> ServerResponse.ok().bodyValue(productBranch))));
    }

    public Mono<ServerResponse> setStock(ServerRequest request) {
        return request.bodyToMono(ProductBranchAssociationDTO.class)
                .flatMap(productBranchAssociationDTO -> parsePathVariables(request)
                        .flatMap(tuple -> productUseCase
                                .setStock(tuple.getT2(), tuple.getT1(), productBranchAssociationDTO.getStock())
                                .flatMap(productBranch -> ServerResponse.ok().bodyValue(productBranch))));
    }

    public Mono<ServerResponse> getMaxStockByFranchise(ServerRequest request) {

        return Mono.fromCallable(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMap(franchiseId -> productUseCase.findMaxStockByFranchise(franchiseId)
                        .flatMap(branches -> ServerResponse.ok().bodyValue(ProductBranchMapper.MAPPER
                                .listBranchesToResponseDto(branches))));
    }

    private Mono<Tuple2<Long, Long>> parsePathVariables(ServerRequest request) {
        return Mono.fromCallable(() -> {
            Long branchId = Long.valueOf(request.pathVariable("branchId"));
            Long productId = Long.valueOf(request.pathVariable("productId"));
            return Tuples.of(branchId, productId);
        });
    }
}
