package co.com.franchise.jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "productBranches")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "product",   fetch = FetchType.EAGER)
    private List<ProductBranchModel> productBranches;
}
