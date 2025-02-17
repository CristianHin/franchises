package co.com.franchise.model.enums;

public enum ErrorCodeMessage {
    FRANCHISE_NOT_FOUND("Franchise not found", "2-404", 404),
    PRODUCT_OR_BRANCH_NOT_FOUND("Product or branch not found", "1-404", FRANCHISE_NOT_FOUND.getStatusCode()),
    INVALID_REQUEST("Invalid request", "400", 400),
    PRODUCT_AND_BRAND_ALREADY_ASSOCIATED("This product is already associated with this branch", "3-400",
            INVALID_REQUEST.getStatusCode()),
    PRODUCT_AND_BRAND_NOT_ASSOCIATED("This product is not associated with this branch", "3-400",
            INVALID_REQUEST.statusCode),
    TECHNICAL_ERROR("Internal error", "500", 500),
    ENTITY_DUPLICATE("The entity you are trying to create is duplicated", "1-400", INVALID_REQUEST.getStatusCode());

    private final String message;
    private final String code;

    private final int statusCode;

    ErrorCodeMessage(String message, String code, int statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
