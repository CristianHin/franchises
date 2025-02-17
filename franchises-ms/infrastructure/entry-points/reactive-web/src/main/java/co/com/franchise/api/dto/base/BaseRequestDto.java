package co.com.franchise.api.dto.base;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BaseRequestDto {
    String name;
}
