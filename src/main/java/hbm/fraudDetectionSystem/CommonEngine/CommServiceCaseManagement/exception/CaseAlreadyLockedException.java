package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.exception;

public class CaseAlreadyLockedException extends Exception{
    public CaseAlreadyLockedException(String message){
        super(message);
    }
}
