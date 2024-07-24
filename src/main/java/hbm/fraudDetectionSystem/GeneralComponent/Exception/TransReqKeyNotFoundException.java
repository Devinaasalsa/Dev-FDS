package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class TransReqKeyNotFoundException extends Exception {
    public TransReqKeyNotFoundException() {
        super("Transaction Request Key not found");
    }
}
