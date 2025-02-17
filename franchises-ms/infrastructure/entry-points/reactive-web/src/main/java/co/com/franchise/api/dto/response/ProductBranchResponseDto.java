package co.com.franchise.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
public class ProductBranchResponseDto {
    ProductResponseDto product;
    Integer stock;
}
