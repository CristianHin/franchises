package co.com.franchise.model.franchise.gateway;

import co.com.franchise.model.franchise.Franchise;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FranchiseRepository {
    Mono<List<Franchise>> getFranchises(int page, int size);
    Mono<Franchise> saveFranchise(Franchise franchise);

    Mono<Franchise> getFranchiseByName(String name);

    Mono<Franchise> getFranchiseById(Long id);
}
