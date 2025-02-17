package co.com.franchise.usecase.product;

import co.com.franchise.model.branch.Branch;
import co.com.franchise.model.branch.gateway.BranchRepository;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import co.com.franchise.model.porduct.Product;
import co.com.franchise.model.porduct.ProductBranch;
import co.com.franchise.model.porduct.gateway.ProductBranchRepository;
import co.com.franchise.model.porduct.gateway.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepository productRepository;
    private final ProductBranchRepository productBranchRepository;
    private final BranchRepository branchRepository;
    public Mono<Product> createProduct(Product product) {
        return productRepository.saveProduct(product);
    }

    public Mono<ProductBranch> associateProductBranch(Long productId, Long branchId, int stock) {
        return productBranchRepository.getProductBranch(productId, branchId)
                .flatMap(productBranch -> Mono.error(new FranchiseException(ErrorCodeMessage.PRODUCT_AND_BRAND_ALREADY_ASSOCIATED)))
                .then((Mono.defer(() -> productBranchRepository.
                        associateProductBranch(productId, branchId, stock))));
    }

    public Mono<Void> disassociateProductBranch(Long productId, Long branchId) {
        return productBranchRepository.getProductBranch(productId, branchId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new FranchiseException(ErrorCodeMessage.PRODUCT_AND_BRAND_NOT_ASSOCIATED))))
                .flatMap(productBranch -> productBranchRepository.disassociateProductBranch(productId, branchId));
    }

    public Mono<ProductBranch> adjustStock(Long productId, Long branchId, int quantity) {
        return productBranchRepository.getProductBranch(productId, branchId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new FranchiseException(ErrorCodeMessage.PRODUCT_AND_BRAND_NOT_ASSOCIATED))))
                .flatMap(productBranch -> {
                    int newStock = productBranch.getStock() + quantity;
                    return productBranchRepository.setStock(productId, branchId, newStock);
                });
    }

    public Mono<ProductBranch> setStock(Long productId, Long branchId, int stock) {
        return productBranchRepository.getProductBranch(productId, branchId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new FranchiseException(ErrorCodeMessage.PRODUCT_AND_BRAND_NOT_ASSOCIATED))))
                .flatMap(productBranch -> productBranchRepository.setStock(productId, branchId, stock));
    }

    public Mono<List<Branch>> findMaxStockByFranchise(Long franchiseId) {
        return branchRepository.getBranchesByFranchiseId(franchiseId)
                .flatMap(branch -> productBranchRepository
                        .findAllWithMaxStockByBranchId(branch.getId())
                        .collectList()
                        .map(productBranches -> branch.toBuilder().products(productBranches).build())
                ).collectList();
    }
}
