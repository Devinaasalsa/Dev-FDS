package hbm.fraudDetectionSystem.GeneralComponent.ExceptionHandler;



import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.exception.BlackListNotFoundException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.exception.*;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueNotFound;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.ListFraudNameNotFound;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(CaseIdNotFoundException.class)
    public ResponseEntity<HttpResponse> caseIdNotFound(CaseIdNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CaseAlreadyLockedException.class)
    public ResponseEntity<HttpResponse>CaseAlreadyLockedException(CaseAlreadyLockedException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CaseAlreadyUnlockedException.class)
    public ResponseEntity<HttpResponse>CaseAlreadyUnlockedException(CaseAlreadyUnlockedException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<HttpResponse>CaseNeedLockException(CaseNeedLockException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ListFraudNameNotFound.class)
    public ResponseEntity<HttpResponse>listSanctionNameNotFound(ListFraudNameNotFound exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BlackListNotFoundException.class)
    public ResponseEntity<HttpResponse>blackListNotFoundException(BlackListNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BlackListExistException.class)
    public ResponseEntity<HttpResponse>blackListExistException(BlackListExistException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(FraudListExistException.class)
    public ResponseEntity<HttpResponse>santionListExistException(FraudListExistException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(FraudValueNotFound.class)
    public ResponseEntity<HttpResponse>sanctionValueNotFound(FraudValueNotFound exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(FraudValueExistException.class)
    public ResponseEntity<HttpResponse>sanctionValueExistException(FraudValueExistException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }


    public ResponseEntity<HttpResponse>createHttpResponse(HttpStatus httpStatus, String message){
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), httpStatus);
    }

}
