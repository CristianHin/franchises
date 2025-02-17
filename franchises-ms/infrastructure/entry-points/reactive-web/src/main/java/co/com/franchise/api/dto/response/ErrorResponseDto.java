package co.com.franchise.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
public class ErrorResponseDto {
    String message;
    String code;
    LocalDateTime timestamp;
}
