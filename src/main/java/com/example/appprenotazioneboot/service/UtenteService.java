package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.dto.UtenteDTO;
import com.example.appprenotazioneboot.dto.UtenteFiltro;
import com.example.appprenotazioneboot.dto.UtenteRequest;
import com.example.appprenotazioneboot.entities.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UtenteService {
    public void inserisciUtente(UtenteRequest utente);
    public Utente loginUtente(String email, String password);
    public List<UtenteDTO> getAllUtenti();
    public Page<UtenteDTO> getUtentiByFilter(UtenteFiltro utenteFiltro, Pageable pageable);
}
