package co.com.franchise.api;

import co.com.franchise.api.dto.request.ProductRequestDto;
import co.com.franchise.api.mapper.ProductMapper;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ProductRequestDto.class)
                .flatMap(productRequestDto -> {
                    Product product = ProductMapper.MAPPER.requestDtoToProduct(productRequestDto);
                    return productUseCase.createProduct(product)
                            .flatMap(productResponse -> ServerResponse
                                    .created(URI.create("/api/v1/products/" + productResponse.getId()))
                                    .bodyValue(ProductMapper.MAPPER.productToResponseDto(productResponse)));
                });
    }
}
