package co.com.franchise.usecase.branch;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.gateway.BranchRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.gateway.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    private BranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BranchUseCase(branchRepository, franchiseRepository);
    }

    @Test
    void shouldSaveBranchSuccessfully() {
        Branch branch = buildBranch();
        Franchise franchise = buildFranchise();

        given(branchRepository.getBranchByName(branch.getName()))
                .willReturn(Mono.empty());
        given(franchiseRepository.getFranchiseById(branch.getFranchiseId()))
                .willReturn(Mono.just(franchise));
        given(branchRepository.saveBranch(any(Branch.class)))
                .willReturn(Mono.just(branch));

        StepVerifier.create(useCase.save(branch))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBranchNameAlreadyExists() {
        Branch branch = buildBranch();

        given(branchRepository.getBranchByName(branch.getName()))
                .willReturn(Mono.just(branch));

        StepVerifier.create(useCase.save(branch))
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    return exception.getErrorCodeMessage() == ErrorCodeMessage.ENTITY_DUPLICATE;
                })
                .verify();
    }

    @Test
    void shouldFailWhenFranchiseNotFound() {

        Branch branch = buildBranch();

        given(branchRepository.getBranchByName(branch.getName()))
                .willReturn(Mono.empty());
        given(franchiseRepository.getFranchiseById(branch.getFranchiseId()))
                .willReturn(Mono.empty());

        StepVerifier.create(useCase.save(branch))
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    return exception.getErrorCodeMessage() == ErrorCodeMessage.FRANCHISE_NOT_FOUND;
                })
                .verify();
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();
    }

    private Franchise buildFranchise() {
        return Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
    }
}
