package co.com.franchise.jpa.repository;

import co.com.franchise.jpa.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductModel, Long> {
    ProductModel findByName(String name);
}
