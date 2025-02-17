package co.com.franchise.model.branch;

import co.com.franchise.model.porduct.ProductBranch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private Long id;
    private String name;
    private List<ProductBranch> products;
    private Long franchiseId;
}
