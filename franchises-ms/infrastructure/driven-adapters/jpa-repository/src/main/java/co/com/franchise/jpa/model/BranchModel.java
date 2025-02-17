package co.com.franchise.jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "branch")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"franchise", "productBranches"})
@NoArgsConstructor
public class BranchModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(  fetch = FetchType.EAGER )
    @JoinColumn(name = "franchise_id")
    private FranchiseModel franchise;

    @OneToMany(mappedBy = "branch",   fetch = FetchType.EAGER)
    private List<ProductBranchModel> productBranches;
}
