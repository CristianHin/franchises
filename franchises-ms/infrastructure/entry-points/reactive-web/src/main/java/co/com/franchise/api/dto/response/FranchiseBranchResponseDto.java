package co.com.franchise.api.dto.response;

import co.com.franchise.api.dto.base.BaseResponseDto;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;


import java.util.List;

@SuperBuilder
@Getter
@ToString
@Jacksonized
public class FranchiseBranchResponseDto extends BaseResponseDto {
    List<BranchResponseDto> branches;
}
