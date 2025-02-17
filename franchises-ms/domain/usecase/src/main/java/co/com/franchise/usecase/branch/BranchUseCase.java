package co.com.franchise.usecase.branch;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.gateway.BranchRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.gateway.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> save(Branch branch) {
        return validateExistingBranchByName(branch.getName())
                .then(Mono.defer(() -> franchiseRepository.getFranchiseById(branch.getFranchiseId())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new FranchiseException(ErrorCodeMessage.FRANCHISE_NOT_FOUND))))
                        .flatMap(franchise -> branchRepository.saveBranch(branch))));
    }

    private Mono<Void> validateExistingBranchByName(String name) {
        return branchRepository.getBranchByName(name)
                .flatMap(franchise ->  Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));
    }
}
