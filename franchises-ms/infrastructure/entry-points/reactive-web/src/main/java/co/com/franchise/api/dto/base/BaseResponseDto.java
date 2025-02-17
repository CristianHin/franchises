package co.com.franchise.api.dto.base;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BaseResponseDto {
    Long id;
    String name;
}
