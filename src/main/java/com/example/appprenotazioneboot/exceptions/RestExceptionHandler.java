package com.example.appprenotazioneboot.exceptions;

import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@ControllerAdvice
@RestController
@Log
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FormErrorException.class)
    public final String exceptionFormErrorHandler(Exception e){
        log.info(e.getMessage());
        return e.getMessage();
    }
}
