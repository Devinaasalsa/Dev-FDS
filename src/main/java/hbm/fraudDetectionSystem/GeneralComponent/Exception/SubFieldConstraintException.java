package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class SubFieldConstraintException extends Exception {
    public SubFieldConstraintException(int size) {
        super(
                String.format(
                        "Child have constraint, with total: %d",
                        size
                )
        );
    }
}
