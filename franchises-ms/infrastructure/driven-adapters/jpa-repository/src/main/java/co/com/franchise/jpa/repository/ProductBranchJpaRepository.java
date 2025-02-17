package co.com.franchise.jpa.repository;

import co.com.franchise.jpa.model.ProductBranchId;
import co.com.franchise.jpa.model.ProductBranchModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductBranchJpaRepository extends JpaRepository<ProductBranchModel, ProductBranchId> {
    @Query("SELECT pb FROM ProductBranchModel pb " +
            "WHERE pb.branch.id = :branchId " +
            "AND pb.stock = (" +
            "    SELECT MAX(p.stock) " +
            "    FROM ProductBranchModel p " +
            "    WHERE p.branch.id = :branchId" +
            ")")
    List<ProductBranchModel> findAllWithMaxStockByBranchId(@Param("branchId") Long branchId);
}
