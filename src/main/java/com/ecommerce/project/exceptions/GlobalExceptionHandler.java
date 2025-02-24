package com.ecommerce.project.exceptions;


import com.ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> methodArgumentNotValid(
            MethodArgumentNotValidException ex)
    {
        Map<String,String> response = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(x->{
            String fieldName =((FieldError)x).getField();
            String message =((FieldError)x).getDefaultMessage();
            response.put(fieldName,message);
        });

        return  new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<APIResponse> insufficientStockException(InsufficientStockException e){
        String message =e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);

        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> resourceNotFoundException(ResourceNotFoundException e){
        String message =e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);

        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> APIException(APIException e){
        String message =e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);

    }


}
