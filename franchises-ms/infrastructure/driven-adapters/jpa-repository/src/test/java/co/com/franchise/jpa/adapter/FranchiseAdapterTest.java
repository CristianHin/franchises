package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.model.FranchiseModel;
import co.com.franchise.jpa.repository.FranchiseJpaRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.Franchise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FranchiseAdapterTest {

    @Mock
    private FranchiseJpaRepository repository;

    private FranchiseAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new FranchiseAdapter(repository);
    }

    @Test
    void shouldSaveFranchise() {

        FranchiseModel franchiseModel = buildModel();
        Franchise franchise = buildDomain();
        when(repository.save(franchiseModel)).thenReturn(franchiseModel);

        Mono<Franchise> franchiseResult = adapter.saveFranchise(franchise);
        StepVerifier.create(franchiseResult)
                .expectNextMatches(franchiseResponse -> {
                    Assertions.assertEquals(franchiseResponse, franchise);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldErrorWhenSaveFranchise() {

        FranchiseModel franchiseModel = buildModel();
        Franchise franchise = buildDomain();
        when(repository.save(franchiseModel)).thenThrow(new DataIntegrityViolationException(""));
        Mono<Franchise> franchiseResult = adapter.saveFranchise(franchise);
        StepVerifier.create(franchiseResult)
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    Assertions.assertEquals(exception.getErrorCodeMessage(), ErrorCodeMessage.ENTITY_DUPLICATE);
                    return true;
                })
                .verify();
    }

    @Test
    void shouldAllEntities() {
        FranchiseModel franchiseModel = buildModel();
        Franchise franchise = buildDomain();
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(franchiseModel),
                        PageRequest.of(0,10),
                        1
                ));
        Mono<List<Franchise>> franchises = adapter.getFranchises(0, 10);
        StepVerifier.create(franchises)
                .expectNextMatches(franchiseResponse -> {
                    Assertions.assertEquals(franchiseResponse.get(0), franchise);
                    return true;
                })
                .verifyComplete();

    }

    @Test
    void shouldReturnFranchiseByName() {
        FranchiseModel franchiseModel = buildModel();
        Franchise franchise = buildDomain();
        when(repository.findByName("")).thenReturn(franchiseModel);
        Mono<Franchise> franchiseResult = adapter.getFranchiseByName("");
        StepVerifier.create(franchiseResult)
                .expectNextMatches(franchiseResponse -> {
                    Assertions.assertEquals(franchiseResponse, franchise);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnFranchiseById() {
        FranchiseModel franchiseModel = buildModel();
        Franchise franchise = buildDomain();
        when(repository.findById(1L)).thenReturn(Optional.of(franchiseModel));
        Mono<Franchise> franchiseResult = adapter.getFranchiseById(1L);
        StepVerifier.create(franchiseResult)
                .expectNextMatches(franchiseResponse -> {
                    Assertions.assertEquals(franchiseResponse, franchise);
                    return true;
                })
                .verifyComplete();
    }
    private FranchiseModel buildModel() {
        FranchiseModel franchiseModel = new FranchiseModel();
        franchiseModel.setId(1L);
        franchiseModel.setName("test");
        return franchiseModel;
    }

    private Franchise buildDomain() {
        return Franchise.builder()
                .id(1L)
                .name("test")
                .build();
    }
}
