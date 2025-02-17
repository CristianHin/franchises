package co.com.franchise.api.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
public class ProductBranchAssociationDTO {
    Integer stock;
}