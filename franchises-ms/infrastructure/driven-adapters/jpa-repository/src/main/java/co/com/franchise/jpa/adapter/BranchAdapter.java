package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.helper.AdapterOperations;
import co.com.franchise.jpa.mapper.BranchMapper;
import co.com.franchise.jpa.model.BranchModel;
import co.com.franchise.jpa.repository.BranchJpaRepository;
import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.gateway.BranchRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.dao.DataIntegrityViolationException;

@Repository
public class BranchAdapter extends AdapterOperations<Branch, BranchModel, Long, BranchJpaRepository>
implements BranchRepository {
    public BranchAdapter(BranchJpaRepository repository) {
        super(repository, BranchMapper.MAPPER);
    }

    @Override
    public Mono<Branch> saveBranch(Branch branch) {
        return Mono.fromCallable(() -> save(branch))
                .onErrorResume(DataIntegrityViolationException.class, e ->
                        Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));
    }

    @Override
    public Mono<Branch> getBranchByName(String name) {
        return Mono.fromCallable(() -> repository.findByName(name))
                .map(mapper::modelToDomain);
    }

    @Override
    public Flux<Branch> getBranchesByFranchiseId(Long franchiseId) {
        return Mono.fromCallable(() -> repository.findByFranchiseId(franchiseId))
                .map(this::toList)
                .flatMapMany(Flux::fromIterable);
    }
}
