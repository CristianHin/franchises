package co.com.franchise.usecase.franchise;

import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.gateway.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<List<Franchise>> getAll(int page, int size) {
        return franchiseRepository.getFranchises(page, size);
    }

    public Mono<Franchise> save(Franchise franchise) {
        return validateExistingFranchiseByName(franchise.getName())
                .then(Mono.defer(() ->franchiseRepository.saveFranchise(franchise)));
    }

    private Mono<Void> validateExistingFranchiseByName(String name) {
        return franchiseRepository.getFranchiseByName(name)
                .flatMap(franchise -> Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));
    }

}
