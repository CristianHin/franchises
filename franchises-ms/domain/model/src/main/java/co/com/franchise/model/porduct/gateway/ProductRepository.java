package co.com.franchise.model.porduct.gateway;

import co.com.franchise.model.porduct.Product;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> saveProduct(Product product);
    Mono<Product> getProductByName(String name);
}
