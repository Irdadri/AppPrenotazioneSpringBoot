package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.entities.Utente;
import org.openapitools.client.model.UtenteHttp;

import org.openapitools.model.Page;
import org.openapitools.model.UtenteDTO;
import org.openapitools.model.UtenteFiltro;
import org.openapitools.model.UtenteRequest;

import org.springframework.data.domain.Pageable;

import java.rmi.NoSuchObjectException;
import java.util.List;

public interface UtenteService {

    public Utente getUtente(String userKey);
    public UtenteHttp getUtenteHttp(String userKey);
    public Page getAllUtenti(Pageable pageable) throws NoSuchObjectException;
    public void creaUtente(String userKey, int idSede);
    public void updateUtente(String userKey, UtenteRequest utenteRequest);
    public UtenteDTO currentUtente(UtenteHttp utenteHttp);
    public void deleteUtente(String userKey);
}
