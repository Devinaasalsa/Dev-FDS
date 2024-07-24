package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class FormatNotFoundException extends Exception {
    public FormatNotFoundException(Long id) {
        super(
                String.format(
                        "Format with id: %s not found.",
                        id
                )
        );
    }
}
