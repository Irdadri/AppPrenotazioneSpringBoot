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


import java.util.List;

public interface PrenotazioneService {
    //da rivedere

    List<PrenotazioneDTO> getPrenotazioni(String idUtente);

    List<PrenotazioneDTO> getAllPrenotazioni();

    List<PrenotazioneDTO> getPrenotazioniUtente(Utente utente);


    Page<PrenotazioneDTO> getAllPrenotazioniWithPaging(int idUtente, Pageable pageable);

    Page<PrenotazioneDTO> getAllPrenotazioniByFilter(PrenotazioniFiltro prenotazioniFiltro, Pageable pageable);

    Page<PrenotazioneDTO> getUtentePrenotazioniByFilter(int idUser, PrenotazioniFiltro prenotazioniFiltro, Pageable pageable);

    PrenotazioneDTO insertPrenotazione(PrenotazioneRequest request, int idUser);

    PrenotazioneDTO getPrenotazioneById(int id);

    PrenotazioneDTO aggiornaPrenotazione(PrenotazioneRequest prenotazioneRequest, int id);


    void deletePrenotazioneById(int id);


}
