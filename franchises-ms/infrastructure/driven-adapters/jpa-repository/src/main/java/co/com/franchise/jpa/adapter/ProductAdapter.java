package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.helper.AdapterOperations;
import co.com.franchise.jpa.mapper.ProductMapper;
import co.com.franchise.jpa.model.ProductModel;
import co.com.franchise.jpa.repository.ProductJpaRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.model.porduct.gateway.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ProductAdapter extends AdapterOperations<Product, ProductModel, Long, ProductJpaRepository>
implements ProductRepository {
    public ProductAdapter(ProductJpaRepository repository) {
        super(repository, ProductMapper.MAPPER);
    }

    @Override
    public Mono<Product> saveProduct(Product product) {
        return Mono.fromCallable(() -> save(product))
                .onErrorResume(DataIntegrityViolationException.class, e ->
                        Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));
    }

    @Override
    public Mono<Product> getProductByName(String name) {
        return Mono.fromCallable(() -> repository.findByName(name))
                .map(mapper::modelToDomain);
    }


}
