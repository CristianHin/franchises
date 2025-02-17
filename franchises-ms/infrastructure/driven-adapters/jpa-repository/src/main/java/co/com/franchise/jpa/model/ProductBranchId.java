package co.com.franchise.jpa.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductBranchId implements Serializable {
    private Long productId;
    private Long branchId;
}
