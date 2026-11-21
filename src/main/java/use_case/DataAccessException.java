package use_case;

/**
 * Thrown by a data access object when it cannot read or write saved data
 * (for example, the account API cannot be reached).
 */
public class DataAccessException extends Exception {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
