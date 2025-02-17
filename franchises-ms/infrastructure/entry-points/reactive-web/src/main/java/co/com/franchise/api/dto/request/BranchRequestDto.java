package co.com.franchise.api.dto.request;

import co.com.franchise.api.dto.base.BaseRequestDto;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@ToString
@Jacksonized
public class BranchRequestDto extends BaseRequestDto {
    Long franchiseId;
}
