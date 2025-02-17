package co.com.franchise.api;

import co.com.franchise.api.dto.request.*;
import co.com.franchise.api.dto.response.BranchResponseDto;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.model.porduct.ProductBranch;
import co.com.franchise.usecase.branch.BranchUseCase;
import co.com.franchise.usecase.franchise.FranchiseUseCase;
import co.com.franchise.usecase.product.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebFluxTest
@ContextConfiguration(classes = {
        RouterRest.class,
        BranchHandler.class,
        FranchiseHandler.class,
        ProductHandler.class,
        ProductBranchHandler.class
})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BranchUseCase branchUseCase;
    @MockitoBean
    private FranchiseUseCase franchiseUseCase;
    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    void shouldCreateBranchSuccessfully() {
        BranchRequestDto requestDto = buildBranchRequestDto();
        Branch savedBranch = buildBranch();

        given(branchUseCase.save(any(Branch.class)))
                .willReturn(Mono.just(savedBranch));

        webTestClient.post()
                .uri("/api/v1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v1/branches/1")
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Branch")
                .jsonPath("$.franchiseId").isEqualTo(1);
    }

    @Test
    void shouldFailWhenBranchNameAlreadyExists() {
        BranchRequestDto requestDto = buildBranchRequestDto();
        given(branchUseCase.save(any(Branch.class)))
                .willReturn(Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));

        webTestClient.post()
                .uri("/api/v1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodeMessage.ENTITY_DUPLICATE.getCode())
                .jsonPath("$.message").isEqualTo(ErrorCodeMessage.ENTITY_DUPLICATE.getMessage())
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldFailWithInvalidRequest() {
        String invalidJson = "{ invalid json }";

        webTestClient.post()
                .uri("/api/v1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodeMessage.INVALID_REQUEST.getCode())
                .jsonPath("$.message").isEqualTo(ErrorCodeMessage.INVALID_REQUEST.getMessage())
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldGetAllFranchisesWithDefaultPagination() {
        List<Franchise> franchises = List.of(buildFranchise());

        given(franchiseUseCase.getAll(0, 10))
                .willReturn(Mono.just(franchises));

        webTestClient.get()
                .uri("/api/v1/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Franchise");
    }

    @Test
    void shouldGetAllFranchisesWithCustomPagination() {
        List<Franchise> franchises = List.of(buildFranchise());

        given(franchiseUseCase.getAll(2, 20))
                .willReturn(Mono.just(franchises));

        webTestClient.get()
                .uri("/api/v1/franchises?page=2&size=20")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Franchise");
    }

    @Test
    void shouldFailWithInvalidPaginationParameters() {
        webTestClient.get()
                .uri("/api/v1/franchises?page=invalid&size=twenty")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodeMessage.INVALID_REQUEST.getCode())
                .jsonPath("$.message").isEqualTo(ErrorCodeMessage.INVALID_REQUEST.getMessage());
    }

    @Test
    void shouldCreateFranchiseSuccessfully() {
        FranchiseRequestDto requestDto = buildFranchiseRequestDto();
        Franchise savedFranchise = buildFranchise();

        given(franchiseUseCase.save(any(Franchise.class)))
                .willReturn(Mono.just(savedFranchise));

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v1/franchises/1")
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Franchise");
    }

    @Test
    void shouldFailWhenFranchiseNameAlreadyExists() {

        FranchiseRequestDto requestDto = buildFranchiseRequestDto();

        given(franchiseUseCase.save(any(Franchise.class)))
                .willReturn(Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));


        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodeMessage.ENTITY_DUPLICATE.getCode())
                .jsonPath("$.message").isEqualTo(ErrorCodeMessage.ENTITY_DUPLICATE.getMessage())
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldAssociateProductToBranch() {
        ProductBranchAssociationDTO requestDto = buildProductBranchAssociationDto();
        ProductBranch productBranch = buildProductBranch();

        given(productUseCase.associateProductBranch(1L, 1L, 100))
                .willReturn(Mono.just(productBranch));

        webTestClient.post()
                .uri("/api/v1/branches/1/products/1/association")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.product.id").isEqualTo(1)
                .jsonPath("$.stock").isEqualTo(100);
    }

    @Test
    void shouldDisassociateProductFromBranch() {
        given(productUseCase.disassociateProductBranch(1L, 1L))
                .willReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/branches/1/products/1/association")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldAdjustStock() {
        AdjustmentStockRequestDto requestDto = AdjustmentStockRequestDto
                .builder()
                .quantity(50)
                .build();
        ProductBranch productBranch = buildProductBranch();

        given(productUseCase.adjustStock(1L, 1L, 50))
                .willReturn(Mono.just(productBranch));

        webTestClient.patch()
                .uri("/api/v1/branches/1/products/1/stock/adjustment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(100);
    }

    @Test
    void shouldSetStock() {
        ProductBranchAssociationDTO requestDto = buildProductBranchAssociationDto();
        ProductBranch productBranch = buildProductBranch();

        given(productUseCase.setStock(1L, 1L, 100))
                .willReturn(Mono.just(productBranch));

        webTestClient.put()
                .uri("/api/v1/branches/1/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(100);
    }

    @Test
    void shouldGetMaxStockByFranchise() {
        List<Branch> branches = List.of(buildBranch());

        given(productUseCase.findMaxStockByFranchise(1L))
                .willReturn(Mono.just(branches));

        webTestClient.get()
                .uri("/api/v1/franchises/1/branches/max-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].products[0].stock").isEqualTo(100);
    }

    @Test
    void shouldFailWithInvalidPathVariable() {
        webTestClient.post()
                .uri("/api/v1/branches/invalid/products/1/association")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildProductBranchAssociationDto())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodeMessage.INVALID_REQUEST.getCode());
    }

    @Test
    void shouldFailWhenProductNotFound() {
        given(productUseCase.associateProductBranch(1L, 1L, 100))
                .willReturn(Mono.error(new FranchiseException(ErrorCodeMessage.PRODUCT_OR_BRANCH_NOT_FOUND)));

        webTestClient.post()
                .uri("/api/v1/branches/1/products/1/association")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildProductBranchAssociationDto())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodeMessage.PRODUCT_OR_BRANCH_NOT_FOUND.getCode());
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductRequestDto requestDto = buildProductRequestDto();
        Product savedProduct = buildProduct();

        given(productUseCase.createProduct(any(Product.class)))
                .willReturn(Mono.just(savedProduct));

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v1/products/1")
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Product");
    }

    private ProductRequestDto buildProductRequestDto() {
        return ProductRequestDto.builder()
                .name("Test Product")
                .build();
    }

    private Product buildProduct() {
        return Product.builder()
                .id(1L)
                .name("Test Product")
                .build();
    }

    private ProductBranchAssociationDTO buildProductBranchAssociationDto() {
        return ProductBranchAssociationDTO.builder().stock(100).build();
    }

    private ProductBranch buildProductBranch() {
        return ProductBranch.builder()
                .product(Product.builder().id(1L).name("Test Product").build())
                .branchId(1L)
                .stock(100)
                .build();
    }


    private Franchise buildFranchise() {
        return Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
    }

    private FranchiseRequestDto buildFranchiseRequestDto() {
        return FranchiseRequestDto.builder()
                .name("Test Franchise")
                .build();
    }

    private BranchRequestDto buildBranchRequestDto() {
        return BranchRequestDto.builder()
                .name("Test Branch")
                .franchiseId(1L)
                .build();
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(1L)
                .name("Test Branch")
                .products(List.of(ProductBranch.builder().stock(100).build()))
                .franchiseId(1L)
                .build();
    }
}