package co.com.franchise.usecase.product;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.gateway.BranchRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.model.porduct.ProductBranch;
import co.com.franchise.model.porduct.gateway.ProductBranchRepository;
import co.com.franchise.model.porduct.gateway.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductBranchRepository productBranchRepository;
    @Mock
    private BranchRepository branchRepository;

    private ProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProductUseCase(productRepository, productBranchRepository, branchRepository);
    }

    @Test
    void shouldCreateProduct() {
        Product product = buildProduct();
        given(productRepository.saveProduct(any(Product.class)))
                .willReturn(Mono.just(product));

        StepVerifier.create(useCase.createProduct(product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldAssociateProductBranch() {
        ProductBranch productBranch = buildProductBranch();
        given(productBranchRepository.getProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.empty());
        given(productBranchRepository.associateProductBranch(anyLong(), anyLong(), any(Integer.class)))
                .willReturn(Mono.just(productBranch));

        StepVerifier.create(useCase.associateProductBranch(1L, 1L, 100))
                .expectNext(productBranch)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenProductAlreadyAssociated() {
        ProductBranch productBranch = buildProductBranch();
        given(productBranchRepository.getProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.just(productBranch));

        StepVerifier.create(useCase.associateProductBranch(1L, 1L, 100))
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    return exception.getErrorCodeMessage() == ErrorCodeMessage.PRODUCT_AND_BRAND_ALREADY_ASSOCIATED;
                })
                .verify();
    }

    @Test
    void shouldDisassociateProductBranch() {
        ProductBranch productBranch = buildProductBranch();
        given(productBranchRepository.getProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.just(productBranch));
        given(productBranchRepository.disassociateProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.empty());

        StepVerifier.create(useCase.disassociateProductBranch(1L, 1L))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenDisassociatingNonAssociatedProduct() {
        given(productBranchRepository.getProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.empty());

        StepVerifier.create(useCase.disassociateProductBranch(1L, 1L))
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    return exception.getErrorCodeMessage() == ErrorCodeMessage.PRODUCT_AND_BRAND_NOT_ASSOCIATED;
                })
                .verify();
    }

    @Test
    void shouldAdjustStock() {
        ProductBranch productBranch = buildProductBranch();
        given(productBranchRepository.getProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.just(productBranch));
        given(productBranchRepository.setStock(anyLong(), anyLong(), any(Integer.class)))
                .willReturn(Mono.just(productBranch));

        StepVerifier.create(useCase.adjustStock(1L, 1L, 50))
                .expectNext(productBranch)
                .verifyComplete();
    }

    @Test
    void shouldSetStock() {
        ProductBranch productBranch = buildProductBranch();
        given(productBranchRepository.getProductBranch(anyLong(), anyLong()))
                .willReturn(Mono.just(productBranch));
        given(productBranchRepository.setStock(anyLong(), anyLong(), any(Integer.class)))
                .willReturn(Mono.just(productBranch));

        StepVerifier.create(useCase.setStock(1L, 1L, 200))
                .expectNext(productBranch)
                .verifyComplete();
    }

    @Test
    void shouldFindMaxStockByFranchise() {
        Branch branch = buildBranch();
        ProductBranch productBranch = buildProductBranch();

        given(branchRepository.getBranchesByFranchiseId(anyLong()))
                .willReturn(Flux.just(branch));
        given(productBranchRepository.findAllWithMaxStockByBranchId(anyLong()))
                .willReturn(Flux.just(productBranch));

        StepVerifier.create(useCase.findMaxStockByFranchise(1L))
                .expectNextMatches(branches -> branches.size() == 1 &&
                            branches.get(0).getProducts().size() == 1
                )
                .verifyComplete();
    }

    private Product buildProduct() {
        return Product.builder()
                .id(1L)
                .name("Test Product")
                .build();
    }

    private ProductBranch buildProductBranch() {
        return ProductBranch.builder()
                .product(buildProduct())
                .branchId(1L)
                .stock(100)
                .build();
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();
    }
}
