package co.com.franchise.jpa.repository;

import co.com.franchise.jpa.model.FranchiseModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseJpaRepository extends JpaRepository<FranchiseModel, Long> {
    FranchiseModel findByName(String name);
}
