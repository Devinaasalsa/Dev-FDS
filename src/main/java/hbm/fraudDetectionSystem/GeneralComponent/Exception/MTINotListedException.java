package hbm.fraudDetectionSystem.GeneralComponent.Exception;

public class MTINotListedException extends Exception {
    public MTINotListedException(Object mti) {
        super(String.format("MTI %S NOT LISTED - STOP PROCESS", mti));
    }
}
