package ma.imane.common_api.exceptions;

public class InsufficientBalanceToDebitException extends RuntimeException {
    public InsufficientBalanceToDebitException(String message) {
        super(message);
    }
}
