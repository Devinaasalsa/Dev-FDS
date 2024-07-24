package hbm.fraudDetectionSystem.TransactionEngine.Exception;

public class TransactionEngineException extends Exception {
    public TransactionEngineException(String message) {
        super(message);
    }

    public TransactionEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionEngineException(Throwable cause) {
        super(cause);
    }
}