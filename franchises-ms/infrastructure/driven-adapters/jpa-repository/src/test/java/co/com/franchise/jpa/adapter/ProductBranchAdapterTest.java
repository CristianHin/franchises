package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.model.BranchModel;
import co.com.franchise.jpa.model.ProductBranchId;
import co.com.franchise.jpa.model.ProductBranchModel;
import co.com.franchise.jpa.model.ProductModel;
import co.com.franchise.jpa.repository.ProductBranchJpaRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.model.porduct.ProductBranch;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductBranchAdapterTest {

    @Mock
    private ProductBranchJpaRepository repository;

    private ProductBranchAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new ProductBranchAdapter(repository);
    }

    @Test
    void shouldAssociateProductBranch() {
        ProductBranchModel model = buildModel();
        ProductBranch domain = buildDomain();

        when(repository.save(any(ProductBranchModel.class))).thenReturn(model);

        Mono<ProductBranch> result = adapter.associateProductBranch(1L, 1L, 100);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    Assertions.assertEquals(response.getProduct().getId(), domain.getProduct().getId());
                    Assertions.assertEquals(response.getStock(), domain.getStock());
                    Assertions.assertEquals(response.getBranchId(), domain.getBranchId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenProductOrBranchNotFound() {
        when(repository.save(any(ProductBranchModel.class)))
                .thenThrow(new JpaObjectRetrievalFailureException(new EntityNotFoundException()));

        Mono<ProductBranch> result = adapter.associateProductBranch(1L, 1L, 100);

        StepVerifier.create(result)
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    Assertions.assertEquals(exception.getErrorCodeMessage(),
                            ErrorCodeMessage.PRODUCT_OR_BRANCH_NOT_FOUND);
                    return true;
                })
                .verify();
    }

    @Test
    void shouldFailWhenAssociationAlreadyExists() {
        when(repository.save(any(ProductBranchModel.class)))
                .thenThrow(new DataIntegrityViolationException(""));

        Mono<ProductBranch> result = adapter.associateProductBranch(1L, 1L, 100);

        StepVerifier.create(result)
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    Assertions.assertEquals(exception.getErrorCodeMessage(),
                            ErrorCodeMessage.ENTITY_DUPLICATE);
                    return true;
                })
                .verify();
    }

    @Test
    void shouldDisassociateProductBranch() {
        Mono<Void> result = adapter.disassociateProductBranch(1L, 1L);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldGetProductBranch() {
        ProductBranchModel model = buildModel();
        ProductBranch domain = buildDomain();

        when(repository.findById(any(ProductBranchId.class)))
                .thenReturn(Optional.of(model));

        Mono<ProductBranch> result = adapter.getProductBranch(1L, 1L);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    Assertions.assertEquals(response.getProduct().getId(), domain.getProduct().getId());
                    Assertions.assertEquals(response.getStock(), domain.getStock());
                    Assertions.assertEquals(response.getBranchId(), domain.getBranchId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindAllWithMaxStockByBranchId() {
        ProductBranchModel model = buildModel();

        when(repository.findAllWithMaxStockByBranchId(1L))
                .thenReturn(List.of(model));

        StepVerifier.create(adapter.findAllWithMaxStockByBranchId(1L))
                .expectNextCount(1)
                .verifyComplete();
    }

    private ProductBranchModel buildModel() {
        ProductBranchModel model = new ProductBranchModel();
        model.setId(new ProductBranchId(1L, 1L));
        model.setStock(100);

        ProductModel productModel = new ProductModel();
        productModel.setId(1L);
        model.setProduct(productModel);

        BranchModel branchModel = new BranchModel();
        branchModel.setId(1L);
        model.setBranch(branchModel);

        return model;
    }

    private ProductBranch buildDomain() {
        return ProductBranch.builder()
                .product(Product.builder().id(1L).build())
                .branchId(1L)
                .stock(100)
                .build();
    }
}