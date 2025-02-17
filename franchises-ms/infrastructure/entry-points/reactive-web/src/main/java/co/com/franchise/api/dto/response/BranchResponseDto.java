package co.com.franchise.api.dto.response;

import co.com.franchise.api.dto.base.BaseResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@SuperBuilder
@Getter
@ToString
@Jacksonized
public class BranchResponseDto extends BaseResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long franchiseId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductBranchResponseDto> products;
}
