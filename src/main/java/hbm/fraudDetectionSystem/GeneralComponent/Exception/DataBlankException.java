package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class DataBlankException extends Exception {
    public DataBlankException(String tagName) {
        super(
                String.format(
                        "%s can't be blank.",
                        tagName
                )
        );
    }

    public DataBlankException() {
        super("Data can't be blank.");
    }
}
