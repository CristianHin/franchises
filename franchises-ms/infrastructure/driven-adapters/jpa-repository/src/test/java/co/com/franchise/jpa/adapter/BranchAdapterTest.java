package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.model.BranchModel;
import co.com.franchise.jpa.repository.BranchJpaRepository;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

class BranchAdapterTest {

    @Mock
    private BranchJpaRepository repository;

    private BranchAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        adapter = new BranchAdapter(repository);
    }

    @Test
    void shouldSaveBranch() {
        BranchModel model = buildModel();
        Branch branch = buildBranch();
        when(repository.save(model)).thenReturn(model);
        Mono<Branch> branchMono = adapter.saveBranch(branch);

        StepVerifier.create(branchMono)
                .expectNextMatches(branchResponse -> {
                    Assertions.assertEquals(branchResponse, branch);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldErrorSaveBranch() {
        BranchModel model = buildModel();
        Branch branch = buildBranch();
        when(repository.save(model)).thenThrow(new DataIntegrityViolationException(""));
        Mono<Branch> branchMono = adapter.saveBranch(branch);

        StepVerifier.create(branchMono)
                .expectErrorMatches(error -> {
                    FranchiseException exception = (FranchiseException) error;
                    Assertions.assertEquals(exception.getErrorCodeMessage(), ErrorCodeMessage.ENTITY_DUPLICATE);
                    return true;
                })
                .verify();
    }

    @Test
    void shouldBranchByName() {
        BranchModel model = buildModel();
        Branch branch = buildBranch();
        when(repository.findByName("")).thenReturn(model);
        Mono<Branch> branchMono = adapter.getBranchByName("");
        StepVerifier.create(branchMono)
                .expectNextMatches(branchResponse -> {
                    Assertions.assertEquals(branchResponse, branch);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldBranchByFranchiseId() {
        BranchModel model = buildModel();
        Branch branch = buildBranch();
        when(repository.findByFranchiseId(1L)).thenReturn(List.of(model));
        Flux<Branch> branchMono = adapter.getBranchesByFranchiseId(1L);
        StepVerifier.create(branchMono)
                .expectNextMatches(branchResponse -> {
                    Assertions.assertEquals(branchResponse, branch);
                    return true;
                })
                .verifyComplete();
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(1L)
                .name("test")
                .build();
    }

    private BranchModel buildModel() {
        BranchModel branchModel = new BranchModel();
        branchModel.setId(1L);
        branchModel.setName("test");
        return branchModel;
    }
}
