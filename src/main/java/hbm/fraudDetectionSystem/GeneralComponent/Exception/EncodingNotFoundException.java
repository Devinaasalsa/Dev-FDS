package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class EncodingNotFoundException extends Exception {
    public EncodingNotFoundException(Long id) {
        super(
                String.format(
                        "Encoding with id: %s not found.",
                        id
                )
        );
    }
}
