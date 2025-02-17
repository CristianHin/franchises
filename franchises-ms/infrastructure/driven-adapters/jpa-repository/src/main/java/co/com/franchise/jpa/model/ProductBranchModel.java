package co.com.franchise.jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "product_branch")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"product", "branch"})
@NoArgsConstructor
public class ProductBranchModel {
    @EmbeddedId
    private ProductBranchId id;

    @ManyToOne  (fetch = FetchType.EAGER)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductModel product;

    @ManyToOne(  fetch = FetchType.EAGER)
    @MapsId("branchId")
    @JoinColumn(name = "branch_id")
    private BranchModel branch;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer stock;
}
