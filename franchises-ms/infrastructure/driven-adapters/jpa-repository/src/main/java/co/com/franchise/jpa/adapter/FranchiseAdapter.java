package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.helper.AdapterOperations;
import co.com.franchise.jpa.mapper.FranchiseMapper;
import co.com.franchise.jpa.model.FranchiseModel;
import co.com.franchise.jpa.repository.FranchiseJpaRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.franchise.Franchise;
import co.com.franchise.model.franchise.gateway.FranchiseRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class FranchiseAdapter extends AdapterOperations<Franchise, FranchiseModel, Long, FranchiseJpaRepository>
        implements FranchiseRepository {
    public FranchiseAdapter(FranchiseJpaRepository repository) {
        super(repository, FranchiseMapper.MAPPER);
    }

    @Override
    public Mono<List<Franchise>> getFranchises(int page, int size) {
        return Mono.fromCallable(() -> findAllPageable(PageRequest.of(page, size)));
    }

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        return Mono.fromCallable(() -> save(franchise))
                .onErrorMap(DataIntegrityViolationException.class,
                        e -> new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE));
    }

    @Override
    public Mono<Franchise> getFranchiseByName(String name) {
        return Mono.fromCallable(() -> repository.findByName(name))
                .map(franchiseModel -> mapper.modelToDomain(franchiseModel));
    }

    @Override
    public Mono<Franchise> getFranchiseById(Long id) {
        return Mono.fromCallable(() -> findById(id));
    }
}
