package com.ecommerce.project.exceptions;

public class CartException extends RuntimeException{
    public CartException(String message){
        super(message);
    }
}
