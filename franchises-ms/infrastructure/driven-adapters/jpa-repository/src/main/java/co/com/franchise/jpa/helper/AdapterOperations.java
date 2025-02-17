package co.com.franchise.jpa.helper;

import co.com.franchise.jpa.mapper.GenericMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import static java.util.stream.StreamSupport.stream;

public abstract class AdapterOperations<E, D, I, R extends JpaRepository<D, I>> {
    protected R repository;
    protected GenericMapper<D, E> mapper;

    protected AdapterOperations(R repository, GenericMapper<D, E> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    protected D toData(E entity) {
        return mapper.domainToModel(entity);
    }

    protected E toEntity(D data) {
        return data != null ? mapper.modelToDomain(data) : null;
    }

    public E save(E entity) {
        D data = toData(entity);
        return toEntity(saveData(data));
    }
    public void delete(I id) {
        repository.deleteById(id);
    }

    public List<E> toList(Iterable<D> iterable) {
        return stream(iterable.spliterator(), false).map(this::toEntity).toList();
    }

    protected D saveData(D data) {
        return repository.save(data);
    }

    public E findById(I id) {
        return toEntity(repository.findById(id).orElse(null));
    }

    public List<E> findAllPageable(Pageable pageable){
        return repository.findAll(pageable)
                .map(this::toEntity)
                .toList();
    }
}
