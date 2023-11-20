package ma.imane.common_api.exceptions;

public class InsufficientCreditAmount extends RuntimeException {
    public InsufficientCreditAmount(String message) {
        super(message);
    }
}
