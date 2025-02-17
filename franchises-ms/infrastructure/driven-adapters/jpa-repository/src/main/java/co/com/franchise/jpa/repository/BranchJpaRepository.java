package co.com.franchise.jpa.repository;

import co.com.franchise.jpa.model.BranchModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchJpaRepository extends JpaRepository<BranchModel, Long> {
    BranchModel findByName(String name);
    List<BranchModel> findByFranchiseId(Long franchiseId);
}
