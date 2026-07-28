package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Utente;
import org.openapitools.model.PrenotazioneDTO;
import org.openapitools.model.PrenotazioneRequest;
import org.openapitools.model.PrenotazioniFiltro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RequestParam;


import java.rmi.NoSuchObjectException;
import java.util.List;

public interface PrenotazioneService {


    Page<PrenotazioneDTO> getAllPrenotazioniWithPaging(String userKey, Pageable pageable);


    Page<PrenotazioneDTO> getAllPrenotazioniByFilter(PrenotazioniFiltro prenotazioniFiltro, Pageable pageable);


    Page<PrenotazioneDTO> getUtentePrenotazioniByFilter(String userKey, PrenotazioniFiltro prenotazioniFiltro, Pageable pageable);

    PrenotazioneDTO insertPrenotazione(PrenotazioneRequest request, String userKey);

    PrenotazioneDTO getPrenotazioneById(int id);

    PrenotazioneDTO aggiornaPrenotazione(PrenotazioneRequest prenotazioneRequest, int id) throws NoSuchObjectException;


    void deletePrenotazioneById(int id) throws Exception;


}
