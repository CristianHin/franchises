package co.com.franchise.api.dto.request;

import co.com.franchise.api.dto.base.BaseRequestDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@ToString
@Jacksonized
public class ProductRequestDto extends BaseRequestDto {
}
