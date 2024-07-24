package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class SubFieldNotHaveConfigurationException extends Exception {

    public SubFieldNotHaveConfigurationException(String fieldNumber) {
        super(
                String.format(
                        "Failed Field: %s, need to check configuration for this field.",
                        fieldNumber
                )
        );
    }
}
