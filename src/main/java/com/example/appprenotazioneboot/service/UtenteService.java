package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.entities.Utente;
import org.openapitools.model.UtenteDTO;
import org.openapitools.model.UtenteFiltro;
import org.openapitools.model.UtenteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UtenteService {

    public void inserisciUtente(UtenteRequest utente);
    public Utente loginUtente(String email, String password);
    public Page<UtenteDTO> getAllUtenti(Pageable pageable);
    public Page<UtenteDTO> getUtentiByFilter(UtenteFiltro utenteFiltro, Pageable pageable);
    public UtenteDTO getUtente(int id);
    public UtenteDTO aggiornaUtente(UtenteRequest utenteRequest, int id);


    public void deleteById(int id);
}
