package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class MsgTypeConfigurationNotFoundException extends Exception {
    public MsgTypeConfigurationNotFoundException() {
        super("Msg Type Configuration not found, please input correct one.");
    }
}
