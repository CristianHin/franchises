package co.com.franchise.jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "franchise")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "branches")
@NoArgsConstructor
public class FranchiseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL,   fetch = FetchType.EAGER)
    private List<BranchModel> branches;
}
