
package com.example.appprenotazioneboot.controller;

import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.exceptions.FormErrorException;
import com.example.appprenotazioneboot.service.PrenotazioneService;
import com.example.appprenotazioneboot.service.SedeService;
import com.example.appprenotazioneboot.service.UtenteService;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.openapitools.api.*;
import org.openapitools.model.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Log
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController implements AggiornaUtenteApi, DefaultApi, DeleteApi,
        DeleteUtenteApi, PrenotazioneApi, SearchPrenotazioniApi,
        SearchPrenotazioniUtenteApi, SignupApi, UtenteApi, UtentiApi {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final SedeService sedeService;
    private final ModelMapper modelMapper;
    private final NativeWebRequest request;


    public DashboardController(PrenotazioneService prenotazioneService, UtenteService utenteService, SedeService sedeService, ModelMapper modelMapper, NativeWebRequest request) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.sedeService = sedeService;
        this.modelMapper = modelMapper;
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(request);
    }

    @Override
    public ResponseEntity<Void> deleteUtente(Integer id) {
        utenteService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page> getUtenti(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(modelMapper.map((utenteService.getAllUtenti(pageable)), Page.class));
    }

    @Override
    public ResponseEntity<UtenteDTO> currentUtente(Integer idUtente) {
        return ResponseEntity.ok(utenteService.getUtente(idUtente));
    }

    @Override
    public ResponseEntity<Void> creaUtente(@Nullable UtenteRequest utenteRequest) {

        utenteService.inserisciUtente(utenteRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page> searchPrenotazioniUtente(Integer idUser, Integer page, Integer size, @Nullable PrenotazioniFiltro prenotazioniFiltro) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(modelMapper.map((prenotazioneService.getUtentePrenotazioniByFilter(idUser, prenotazioniFiltro, pageable)), Page.class));
    }



    @Override
    public ResponseEntity<Page> searchPrenotazioni(Integer page, Integer size, @Nullable PrenotazioniFiltro prenotazioniFiltro) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(modelMapper.map((prenotazioneService.getAllPrenotazioniByFilter(prenotazioniFiltro, pageable)), Page.class));
    }

    @Override
    public ResponseEntity<PrenotazioneDTO> creaPrenotazione(Integer idUser, PrenotazioneRequest prenotazioneRequest) {
        PrenotazioneDTO prenotazioneDTO = prenotazioneService.insertPrenotazione(prenotazioneRequest, idUser);
        if (prenotazioneDTO != null) {
            return ResponseEntity.ok(prenotazioneDTO);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(prenotazioneDTO);
        }
    }

    @Override
    public ResponseEntity<PrenotazioneDTO> currentPrenotazione(Integer idPrenotazione) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(idPrenotazione));
    }

    @Override
    public ResponseEntity<Void> deletePrenotazione(Integer id) {
        prenotazioneService.deletePrenotazioneById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page> getDashboard(Integer idUtente, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(modelMapper.map((prenotazioneService.getAllPrenotazioniWithPaging(idUtente, pageable)), Page.class));
    }

    @Override
    public ResponseEntity<UtenteDTO> updateUtente(Integer idUser, UtenteRequest utenteRequest) {
        UtenteDTO utenteDTO = utenteService.aggiornaUtente(utenteRequest, idUser);
        if (utenteDTO != null) {
            return ResponseEntity.ok(utenteDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
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





