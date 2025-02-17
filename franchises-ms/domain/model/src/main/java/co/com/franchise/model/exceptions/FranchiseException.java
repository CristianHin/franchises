package co.com.franchise.model.exceptions;

import co.com.franchise.model.enums.ErrorCodeMessage;

public class FranchiseException extends RuntimeException {
    private final ErrorCodeMessage errorCodeMessage;

    public FranchiseException(ErrorCodeMessage errorsCodeMessage){
        super(errorsCodeMessage.getMessage());
        this.errorCodeMessage = errorsCodeMessage;
    }
    public ErrorCodeMessage getErrorCodeMessage(){
        return errorCodeMessage;
    }
}
