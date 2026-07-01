package com.example.appprenotazioneboot.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
public class FormErrorException extends Exception {


    public FormErrorException() {
        super("errore nel form");
    }

    public FormErrorException(String msg){
        super(msg);
    }
}
