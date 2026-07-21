package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.dto.UtenteDTO;
import com.example.appprenotazioneboot.dto.UtenteFiltro;
import com.example.appprenotazioneboot.dto.UtenteRequest;
import com.example.appprenotazioneboot.entities.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UtenteService {
    public void inserisciUtente(UtenteRequest utente) throws Exception;
    public Utente loginUtente(String email, String password);
    public Page<UtenteDTO> getAllUtenti(Pageable pageable);
    public Page<UtenteDTO> getUtentiByFilter(UtenteFiltro utenteFiltro, Pageable pageable);
    public UtenteDTO getUtente(int id);
    public UtenteDTO aggiornaUtente(UtenteRequest utenteRequest, int id);
    public void deleteById(int id);
    public Utente getUtenteByEmail(String email);
}
