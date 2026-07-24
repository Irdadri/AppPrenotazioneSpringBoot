package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.entities.Utente;
import org.openapitools.model.UtenteDTO;
import org.openapitools.model.UtenteFiltro;
import org.openapitools.model.UtenteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UtenteService {

    public Utente getUtente(String unique);
}
