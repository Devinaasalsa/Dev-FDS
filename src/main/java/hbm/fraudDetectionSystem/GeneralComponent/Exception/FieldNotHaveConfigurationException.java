package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class FieldNotHaveConfigurationException extends Exception {

    public FieldNotHaveConfigurationException(int fieldNumber) {
        super(
                String.format(
                        "Field: %s is mandatory from BITMAP, need to set configuration for this field.",
                        fieldNumber
                )
        );
    }
}
