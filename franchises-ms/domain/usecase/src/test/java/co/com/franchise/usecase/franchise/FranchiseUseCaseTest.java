package co.com.franchise.usecase.franchise;

import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.gateway.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private FranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FranchiseUseCase(franchiseRepository);
    }

    @Test
    void shouldGetAllFranchises() {
        List<Franchise> franchises = List.of(buildFranchise());
        given(franchiseRepository.getFranchises(anyInt(), anyInt()))
                .willReturn(Mono.just(franchises));

        StepVerifier.create(useCase.getAll(0, 10))
                .expectNext(franchises)
                .verifyComplete();
    }

    @Test
    void shouldSaveFranchiseSuccessfully() {
        Franchise franchise = buildFranchise();
        given(franchiseRepository.getFranchiseByName(anyString()))
                .willReturn(Mono.empty());
        given(franchiseRepository.saveFranchise(any(Franchise.class)))
                .willReturn(Mono.just(franchise));

        StepVerifier.create(useCase.save(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenFranchiseNameAlreadyExists() {
        Franchise franchise = buildFranchise();
        given(franchiseRepository.getFranchiseByName(anyString()))
                .willReturn(Mono.just(franchise));

        StepVerifier.create(useCase.save(franchise))
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    return exception.getErrorCodeMessage() == ErrorCodeMessage.ENTITY_DUPLICATE;
                })
                .verify();
    }

    private Franchise buildFranchise() {
        return Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
    }
}