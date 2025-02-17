package co.com.franchise.model.branch.gateway;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.franchise.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {

    Mono<Branch> saveBranch(Branch branch);

    Mono<Branch> getBranchByName(String name);

    Flux<Branch> getBranchesByFranchiseId(Long franchiseId);
}
