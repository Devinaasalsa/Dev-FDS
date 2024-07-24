package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class MsgConfigurationNotFoundException extends Exception {
    public MsgConfigurationNotFoundException() {
        super("Msg Configuration not found, please input correct one.");
    }
}
