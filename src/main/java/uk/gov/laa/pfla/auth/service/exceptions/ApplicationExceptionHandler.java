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

    private static final String ERROR_STRING = "Error: ";
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(CsvStreamException.class)
    public ResponseEntity<Map<String, String>> handleCsvStreamException(CsvStreamException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(DatabaseReadException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseReadException(DatabaseReadException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    @ExceptionHandler(ReportIdNotFoundException.class)
    public ResponseEntity<Map<String, String>>  handleReportIdNotFoundException(ReportIdNotFoundException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<Map<String, String>> handleIndexOutOfBoundsException(IndexOutOfBoundsException e){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_STRING, e.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);

    }
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        String message = ERROR_STRING + "Invalid input for parameter " + e.getName() + ". Expected a numeric value";
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(message, e.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

}
