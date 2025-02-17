package co.com.franchise.model.porduct;

import co.com.franchise.model.porduct.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductBranch {
    private Product product;
    private int stock;
    private Long branchId;
}
