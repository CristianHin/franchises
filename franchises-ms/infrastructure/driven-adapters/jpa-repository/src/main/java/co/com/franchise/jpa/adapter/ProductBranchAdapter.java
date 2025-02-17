package co.com.franchise.jpa.adapter;

import co.com.franchise.jpa.helper.AdapterOperations;
import co.com.franchise.jpa.mapper.ProductBranchMapper;
import co.com.franchise.jpa.model.ProductBranchId;
import co.com.franchise.jpa.model.ProductBranchModel;
import co.com.franchise.jpa.repository.ProductBranchJpaRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.model.porduct.ProductBranch;
import co.com.franchise.model.porduct.gateway.ProductBranchRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

@Repository
public class ProductBranchAdapter extends AdapterOperations<ProductBranch, ProductBranchModel, ProductBranchId,
        ProductBranchJpaRepository> implements ProductBranchRepository {
    public ProductBranchAdapter(ProductBranchJpaRepository repository) {
        super(repository, ProductBranchMapper.MAPPER);
    }

    @Override
    public Mono<ProductBranch> associateProductBranch(Long productId, Long branchId, int stock) {
        Product product = Product.builder().id(productId).build();
        ProductBranch productBranch = ProductBranch.builder()
                .branchId(branchId)
                .product(product)
                .stock(stock)
                .build();
        return Mono.fromCallable(() -> save(productBranch))
                .onErrorResume(JpaObjectRetrievalFailureException.class, e ->
                        Mono.error(new FranchiseException(ErrorCodeMessage.PRODUCT_OR_BRANCH_NOT_FOUND)))
                .onErrorResume(DataIntegrityViolationException.class, e ->
                        Mono.error(new FranchiseException(ErrorCodeMessage.ENTITY_DUPLICATE)));
    }

    @Override
    public Mono<Void> disassociateProductBranch(Long productId, Long branchId) {
        ProductBranchId id = new ProductBranchId(productId, branchId);
        return Mono.fromRunnable(() -> delete(id));
    }

    @Override
    public Mono<ProductBranch> getProductBranch(Long productId, Long branchId) {
        ProductBranchId id = new ProductBranchId(productId, branchId);
        return Mono.fromCallable(() -> findById(id));
    }

    @Override
    public Mono<ProductBranch> setStock(Long productId, Long branchId, int stock) {
        return this.associateProductBranch(productId, branchId, stock);
    }

    @Override
    public Flux<ProductBranch> findAllWithMaxStockByBranchId(Long branchId) {
        return Mono.fromCallable(() -> repository.findAllWithMaxStockByBranchId(branchId))
                .map(this::toList)
                .flatMapMany(Flux::fromIterable);
    }
}
