package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.SedeRepository;
import com.example.appprenotazioneboot.repository.UtenteRepository;
import jakarta.persistence.criteria.Join;
import org.modelmapper.ModelMapper;
import org.openapitools.model.UtenteDTO;
import org.openapitools.model.UtenteFiltro;
import org.openapitools.model.UtenteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtenteServiceImpl implements  UtenteService{
    private UtenteRepository repository;

    public UtenteServiceImpl(UtenteRepository repository) {
        this.repository = repository;
    }



/*
    public void inserisciUtente(UtenteRequest utenteRequest) {
        try {
            utenteRequest.setPassword(passwordEncrypting(utenteRequest.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Utente utente = modelMapper.map(utenteRequest, Utente.class);

        if (utenteRequest.getIdSede() != null) {
            Sede sede = sedeRepository.findSedeById(Math.toIntExact(utenteRequest.getIdSede()));
            if(sede != null) {
                utente.setSede(sede);
            } else {
                utente.setSede(new Sede());
            }
        } else {
            utente.setSede(new Sede());
        }
        repository.save(utente);
    }
    */
    /*

    public Page<UtenteDTO> getAllUtenti(Pageable pageable){
        Page<Utente> utenti = repository.findAll(pageable);
        return utenti.map(utente -> modelMapper.map(utente, UtenteDTO.class));
    }

     */


    @Override
    public Utente getUtente(String unique) {
        return repository.findUtenteByUnique(unique);
    }
/*
    @Override
    public UtenteDTO aggiornaUtente(UtenteRequest utenteRequest, int id) {
        Utente utente = repository.findUtenteById(id);
        if(utente != null){
            if(utenteRequest.getNome() != null){
                utente.setNome(utenteRequest.getNome());
            }
            if(utenteRequest.getCognome() != null){
                utente.setCognome(utenteRequest.getCognome());
            }
            if(utenteRequest.getPassword() != null){
                utente.setPassword(utente.getPassword());
            }
            if(utenteRequest.getEmail() != null){
                utente.setEmail(utente.getEmail());
            }
            if(utenteRequest.getTelefono() != null){
                utente.setTelefono(utente.getTelefono());
            }
            if(utenteRequest.getTipoUtente() != null){
                utente.setTipoUtente(TipoUtenteEnum.valueOf(utenteRequest.getTipoUtente()));
            }
            if(utenteRequest.getIdSede() != null){
                utente.setSede(sedeRepository.findSedeById(utenteRequest.getIdSede()));
            }

            repository.save(utente);
            return modelMapper.map(utente, UtenteDTO.class);
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        Utente utente = repository.findUtenteById(id);
        if(utente != null){
            repository.delete(utente);
        }
    }

 */

}
