package co.com.franchise.model.porduct.gateway;

import co.com.franchise.model.porduct.ProductBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductBranchRepository {
    Mono<ProductBranch> associateProductBranch(Long productId, Long branchId, int stock);
    Mono<Void> disassociateProductBranch(Long productId, Long branchId);
    Mono<ProductBranch> getProductBranch(Long productId, Long branchId);

    Mono<ProductBranch> setStock(Long productId, Long branchId, int stock);

    Flux<ProductBranch> findAllWithMaxStockByBranchId(Long branchId);
}
