package hbm.fraudDetectionSystem.GeneralComponent.Domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public abstract class ResponseResourceEntity<T> {
    public ResponseEntity<HttpResponse<T>> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase()), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseWithData(HttpStatus httpStatus, String message, T responseData) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<Object>> responseWithDataObject(HttpStatus httpStatus, String message, Object responseData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<List<T>>> responseWithListData(HttpStatus httpStatus, String message, List<T> responseData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<?>> responseWithListObjectData(HttpStatus httpStatus, String message, Object responseData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<Set<T>>> responseWithSetData(HttpStatus httpStatus, String message, Set<T> responseData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseHeader(HttpStatus httpStatus, String message, HttpHeaders headers) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase()), headers, httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseWithDataHeader(HttpStatus httpStatus, String message, T responseData, HttpHeaders headers) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), headers, httpStatus);
    }
}
