package co.com.franchise.api;

import co.com.franchise.api.dto.response.ErrorResponseDto;
import co.com.franchise.model.enums.ErrorCodeMessage;
import co.com.franchise.model.exceptions.FranchiseException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler franchiseHandler, BranchHandler branchHandler, ProductHandler productHandler, ProductBranchHandler productBranchHandler) {
        return route(GET("/api/v1/franchises"), franchiseHandler::getAll)
                .andRoute(POST("/api/v1/franchises"), franchiseHandler::save)
                .andRoute(GET("/api/v1/franchises/{franchiseId}/branches/max-stock"),
                        productBranchHandler::getMaxStockByFranchise)
                .andRoute(POST("/api/v1/branches/{branchId}/products/{productId}/association"),
                        productBranchHandler::associateProductFromBranch)
                .andRoute(DELETE("/api/v1/branches/{branchId}/products/{productId}/association"),
                        productBranchHandler::disassociateProductFromBranch)
                .andRoute(PATCH("/api/v1/branches/{branchId}/products/{productId}/stock/adjustment"),
                        productBranchHandler::adjustStock)
                .andRoute(PUT("/api/v1/branches/{branchId}/products/{productId}/stock"),
                        productBranchHandler::setStock)
                .andRoute(POST("/api/v1/branches"), branchHandler::save)
                .andRoute(POST("/api/v1/products"), productHandler::save)
                .filter(handler());
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> handler() {
        return (request, next) -> {
            log.info("Incoming request - Method: [{}], Path: [{}]", request.method(), request.path());
            return next.handle(request).onErrorResume(error -> this.handleError(error, request));
        };
    }

    private Mono<ServerResponse> handleError(Throwable error, ServerRequest request) {
        if (error instanceof FranchiseException) {
            ErrorCodeMessage errorCodeMessage = ((FranchiseException) error).getErrorCodeMessage();
            return createErrorResponse(HttpStatus.valueOf(errorCodeMessage.getStatusCode()), errorCodeMessage.getMessage(), errorCodeMessage.getCode());
        }

        log.error("Failed to handle error in path: {}, error: {}", request.path(), error.getMessage());

        if (error instanceof NumberFormatException
                || error instanceof DecodingException
                || error instanceof JsonParseException
                || error instanceof JsonMappingException
                || error instanceof ServerWebInputException) {
            ErrorCodeMessage errorCodeMessage = ErrorCodeMessage.INVALID_REQUEST;
            return createErrorResponse(HttpStatus.valueOf(errorCodeMessage.getStatusCode()), errorCodeMessage.getMessage(), errorCodeMessage.getCode());
        }

        ErrorCodeMessage errorCodeMessage = ErrorCodeMessage.TECHNICAL_ERROR;
        return createErrorResponse(HttpStatus.valueOf(errorCodeMessage.getStatusCode()), errorCodeMessage.getMessage(), errorCodeMessage.getCode());
    }


    private Mono<ServerResponse> createErrorResponse(HttpStatus status, String message, String code) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
        return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).bodyValue(errorResponseDto);
    }
}
