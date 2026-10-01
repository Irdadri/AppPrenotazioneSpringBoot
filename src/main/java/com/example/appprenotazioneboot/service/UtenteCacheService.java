/*
package com.example.appprenotazioneboot.service;


import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UtenteCacheService {

    private final UtenteApi utenteApi;

    public UtenteCacheService(UtenteApi utenteApi) {
        this.utenteApi = utenteApi;
    }

    @Cacheable(value = "utenti", key = "#userKey")
    public UtenteHttp getCurrentUtente(String userKey) {
        return utenteApi.getCurrentUtente(userKey);
    }

    @Cacheable(value="utenti", key = "#email")
    public UtenteHttp getHttpUser(String email){
        return utenteApi.getHttpUser(email);
    }
}

*/