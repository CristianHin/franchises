package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.model.ProductModel;
import co.com.franchise.jpa.repository.ProductJpaRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.porduct.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;


class ProductAdapterTest {

    @Mock
    private ProductJpaRepository repository;

    private ProductAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        adapter = new ProductAdapter(repository);
    }

    @Test
    void shouldSaveProduct() {
        Product product = buildProduct();
        ProductModel model = buildModel();
        when(repository.save(model)).thenReturn(model);
        Mono<Product> result = adapter.saveProduct(product);
        StepVerifier.create(result)
                .expectNextMatches(productResponse -> {
                    Assertions.assertEquals(productResponse, product);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldErrorSaveProduct() {
        Product product = buildProduct();
        ProductModel model = buildModel();
        when(repository.save(model)).thenThrow(new DataIntegrityViolationException(""));
        Mono<Product> result = adapter.saveProduct(product);
        StepVerifier.create(result)
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    Assertions.assertEquals(exception.getErrorCodeMessage(), ErrorCodeMessage.ENTITY_DUPLICATE);
                    return true;
                })
                .verify();
    }

    @Test
    void shouldGetProductByName() {
        Product product = buildProduct();
        ProductModel model = buildModel();
        when(repository.findByName("")).thenReturn(model);
        Mono<Product> result = adapter.getProductByName("");
        StepVerifier.create(result)
                .expectNextMatches(productResponse -> {
                    Assertions.assertEquals(productResponse, product);
                    return true;
                })
                .verifyComplete();
    }

    private Product buildProduct() {
        return Product.builder()
                .id(1L)
                .name("test")
                .build();
    }

    private ProductModel buildModel() {
        ProductModel model = new ProductModel();
        model.setId(1L);
        model.setName("test");
        return model;
    }

}
