package uk.gov.laa.pfla.auth.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApplicationExceptionHandler {

    private static final String ERROR_STRING = "error";
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CsvStreamException.class)
    public Map<String, String> handleCsvStreamException(CsvStreamException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return errorMap;

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DatabaseReadException.class)
    public Map<String, String> handleDatabaseReadException(DatabaseReadException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return errorMap;

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReportIdNotFoundException.class)
    public Map<String, String> handleReportIdNotFoundException(ReportIdNotFoundException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return errorMap;

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Map<String, String> handleIndexOutOfBoundsException(IndexOutOfBoundsException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return errorMap;

    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        String message = "Invalid input for parameter " + e.getName() + ". Expected a numeric value";

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

}
