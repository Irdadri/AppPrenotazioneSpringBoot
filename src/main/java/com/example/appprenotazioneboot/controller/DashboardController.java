
package com.example.appprenotazioneboot.controller;

import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.service.PrenotazioneService;
import com.example.appprenotazioneboot.service.SedeService;
import com.example.appprenotazioneboot.service.UtenteService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.openapitools.api.*;

import org.openapitools.client.model.UtenteHttp;
import org.openapitools.model.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;


import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Log
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController implements AggiornaUtenteApi, AggiornaPrenotazioneApi, DefaultApi, DeleteApi,
        PrenotazioneApi, SearchPrenotazioniApi,
        SearchPrenotazioniUtenteApi, SignupApi, UtenteApi, UtentiApi,
        DeleteUserApi {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final SedeService sedeService;
    private final ModelMapper modelMapper;
    private final NativeWebRequest request;
    private final org.openapitools.client.api.UtenteApi utenteApi;


    public DashboardController(PrenotazioneService prenotazioneService, UtenteService utenteService, SedeService sedeService, ModelMapper modelMapper, NativeWebRequest request, org.openapitools.client.api.UtenteApi utenteApi) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.sedeService = sedeService;
        this.modelMapper = modelMapper;
        this.request = request;
        this.utenteApi = utenteApi;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(request);
    }


    @Override
    public ResponseEntity<Void> deleteUtente(String userKey) {
        try {
            utenteApi.deleteUtente(userKey);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
        utenteService.deleteUtente(userKey);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page> getUtenti(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);


        //org.openapitools.client.model.Page utenti = utenteApi.getAllUtenti(page, size);

        try {
            Page utenti = utenteService.getAllUtenti(pageable);
            return ResponseEntity.ok(utenti);
        } catch (NoSuchObjectException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public ResponseEntity<UtenteDTO> currentUtente(String userKey) {
        try {
            UtenteHttp utenteHttp = utenteApi.getCurrentUtente(userKey);
            log.info(utenteHttp.toString());
            UtenteDTO utenteDTO = utenteService.currentUtente(utenteHttp);
            return ResponseEntity.ok(utenteDTO);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> creaUtente(@Nullable UtenteRequest utenteRequest) {

        org.openapitools.client.model.UtenteRequest request = modelMapper.map(utenteRequest, org.openapitools.client.model.UtenteRequest.class);
        String unique = utenteApi.creaUtente(request);
        if (utenteRequest.getIdSede() != null) {
            utenteService.creaUtente(unique, utenteRequest.getIdSede());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @Override
    public ResponseEntity<Page> searchPrenotazioniUtente(String userKey, Integer page, Integer size, @Nullable PrenotazioniFiltro prenotazioniFiltro) {
        Pageable pageable = PageRequest.of(page, size);
        Page prenotazioni = modelMapper.map((prenotazioneService.getUtentePrenotazioniByFilter(userKey, prenotazioniFiltro, pageable)), Page.class);
        if (prenotazioni == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prenotazioni);
    }

    @Override
    public ResponseEntity<Void> updateUtente(String userKey, UtenteRequest utenteRequest) {
        if (userKey != null) {
            org.openapitools.client.model.UtenteRequest request = modelMapper.map(utenteRequest, org.openapitools.client.model.UtenteRequest.class);
            try {
                utenteApi.updateUtente(userKey, request);
                utenteService.updateUtente(userKey, utenteRequest);
                return ResponseEntity.ok().build();
            } catch (HttpClientErrorException.NotFound e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

    }


    @Override
    public ResponseEntity<Page> searchPrenotazioni(Integer page, Integer size, @Nullable PrenotazioniFiltro prenotazioniFiltro) {
        Pageable pageable = PageRequest.of(page, size);

        Page prenotazioni = modelMapper.map((prenotazioneService.getAllPrenotazioniByFilter(prenotazioniFiltro, pageable)), Page.class);
        if (prenotazioni == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prenotazioni);
    }

    @Override
    public ResponseEntity<PrenotazioneDTO> creaPrenotazione(String userKey, PrenotazioneRequest prenotazioneRequest) {
        PrenotazioneDTO prenotazioneDTO = prenotazioneService.insertPrenotazione(prenotazioneRequest, userKey);
        return ResponseEntity.ok(prenotazioneDTO);
    }

    @Override
    public ResponseEntity<PrenotazioneDTO> currentPrenotazione(Integer idPrenotazione) {
        PrenotazioneDTO prenotazioneDTO = prenotazioneService.getPrenotazioneById(idPrenotazione);
        return ResponseEntity.ok(prenotazioneDTO);
    }

    @Override
    public ResponseEntity<Void> deletePrenotazione(Integer id) {

        prenotazioneService.deletePrenotazioneById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page> getDashboard(String userKey, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<PrenotazioneDTO> list = (prenotazioneService.getAllPrenotazioniWithPaging(userKey, pageable));
        Page prenotazioni = modelMapper.map(list, Page.class);
        if (prenotazioni == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prenotazioni);
    }

    @Override
    public ResponseEntity<Void> updatePrenotazione(Integer idPrenotazione, PrenotazioneRequest prenotazioneRequest) {

        prenotazioneService.aggiornaPrenotazione(prenotazioneRequest, idPrenotazione);


        return ResponseEntity.ok().build();
    }


    //dati statici per i form
    @GetMapping("/listaSedi")
    public ResponseEntity<List<Sede>> getListaSedi() {
        return ResponseEntity.ok(sedeService.getAllSedi());
    }


    @GetMapping("/listaRuoli")
    public ResponseEntity<TipoUtenteEnum[]> getRuoliUtente() {
        return ResponseEntity.ok(TipoUtenteEnum.values());
    }
}





