package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.SedeRepository;
import com.example.appprenotazioneboot.repository.UtenteRepository;

import jakarta.persistence.criteria.Join;
import org.modelmapper.ModelMapper;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.openapitools.model.UtenteDTO;
import org.openapitools.model.UtenteFiltro;
import org.openapitools.model.UtenteRequest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.List;

@Service
public class UtenteServiceImpl implements UtenteService{
    private final ModelMapper modelMapper;
    private final SedeRepository sedeRepository;
    private UtenteRepository repository;
    private final UtenteApi utenteApi;

    private final UtenteCacheService utenteCacheService;

    public UtenteServiceImpl(UtenteRepository repository, ModelMapper modelMapper, SedeRepository sedeRepository, UtenteApi utenteApi, UtenteCacheService utenteCacheService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.sedeRepository = sedeRepository;
        this.utenteApi = utenteApi;
        this.utenteCacheService = utenteCacheService;
    }


    @Override
    public void creaUtente(String userKey, int idSede) {
        Utente utente = new Utente();
        utente.setUserKey(userKey);
        utente.setSede(sedeRepository.findSedeById(idSede));
        repository.save(utente);
    }

    @Override
    public Utente getUtente(String userKey) {
        return repository.findUtenteByUserKey(userKey);
    }

    @Override
    public UtenteHttp getUtenteHttp(String userKey){
       return utenteApi.getCurrentUtente(userKey);
    }

    @Override
    @Cacheable(value = "allBookings", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public org.openapitools.model.Page getAllUtenti(Pageable pageable) throws NoSuchObjectException {
        Page<UtenteDTO> utenteDTOPage = repository.findAll(pageable)
                .map(utente -> {
                    UtenteHttp utenteHttp = utenteCacheService.getUtenteHttp(utente.getUserKey());
                    UtenteDTO temp = modelMapper.map(utenteHttp, UtenteDTO.class);
                    Sede sede = sedeRepository.findSedeById(utente.getSede().getId());
                    temp.setCitta(sede.getCitta());
                    temp.setIndirizzo(sede.getIndirizzo());
                    temp.setRegione(sede.getRegione());
                    return temp;
                });

        if(utenteDTOPage != null){
            return modelMapper.map(utenteDTOPage, org.openapitools.model.Page.class);
        } else {
            throw new NoSuchObjectException("utenti non trovati");
        }

    }

    @Override
    public void updateUtente(String userKey, UtenteRequest utenteRequest) {
        Utente utente = getUtente(userKey);
        if(utente != null) {
            if (utenteRequest.getIdSede() != null) {
                utente.setSede(sedeRepository.findSedeById(utenteRequest.getIdSede()));
            }

            repository.save(utente);
        }
    }

    @Override
    public UtenteDTO currentUtente(UtenteHttp utenteHttp) {
        Utente utente = getUtente(utenteHttp.getUserKey());
        UtenteDTO utenteDTO = modelMapper.map(utenteHttp, UtenteDTO.class);
        utenteDTO.setRegione(utente.getSede().getRegione());
        utenteDTO.setPaese(utente.getSede().getPaese());
        utenteDTO.setIndirizzo(utente.getSede().getIndirizzo());
        utenteDTO.setCitta(utente.getSede().getIndirizzo());
        return utenteDTO;
    }


    @Override
    public void deleteUtente(String userKey) {
        Utente utente = getUtente(userKey);
        if(utente != null){
            repository.delete(utente);
        }
    }



}
