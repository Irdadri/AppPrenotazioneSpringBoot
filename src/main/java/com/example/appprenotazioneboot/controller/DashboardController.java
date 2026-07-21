package com.example.appprenotazioneboot.controller;

import com.example.appprenotazioneboot.dto.*;
import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.exceptions.FormErrorException;
import com.example.appprenotazioneboot.service.PrenotazioneService;
import com.example.appprenotazioneboot.service.SedeService;
import com.example.appprenotazioneboot.service.UtenteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final HttpSession session;
    private final SedeService sedeService;

    public DashboardController(PrenotazioneService prenotazioneService, UtenteService utenteService, HttpSession session, SedeService sedeService) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.session = session;
        this.sedeService = sedeService;
    }

    //dashboard utente o manager
    @GetMapping("/{idUtente}")
    public ResponseEntity<List<PrenotazioneDTO>> getDashboard(@PathVariable String idUtente) {
        return new ResponseEntity<List<PrenotazioneDTO>>(prenotazioneService.getPrenotazioni(idUtente), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> getDashboard(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @RequestParam int idUser) {
        Pageable pageable = PageRequest.of(page, size);
        //Utente utente = (Utente) session.getAttribute("utente");
        return ResponseEntity.ok(prenotazioneService.getAllPrenotazioniWithPaging(idUser, pageable));
    }

/*
    //endpoint test per spring data paging
    @GetMapping("/test")
    public ResponseEntity<?> listaPrenotazioniPaging() {
        Pageable pageable = PageRequest.of(0, 5);

        return ResponseEntity.ok(prenotazioneService.getAllPrenotazioniWithPaging( pageable));
    }

 */

    //dati statici per i form
    @GetMapping("/listaSedi")
    public ResponseEntity<List<Sede>> getListaSedi() {
        return ResponseEntity.ok(sedeService.getAllSedi());
    }

    @GetMapping("/listaRuoli")
    public ResponseEntity<TipoUtenteEnum[]> getRuoliUtente() {
        return ResponseEntity.ok(TipoUtenteEnum.values());
    }


    //crea utente
    @PostMapping("/signup")
    public ResponseEntity<?> creaUtente(@Valid @RequestBody UtenteRequest utenteRequest,
                                        BindingResult bindingResult) throws FormErrorException {

        if (bindingResult.hasErrors()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("errore nel form");
            throw new FormErrorException();
        }

        try {
            utenteService.inserisciUtente(utenteRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    //restituisce la lista completa degli utenti
    @GetMapping("/utenti")
    public ResponseEntity<?> getUtenti(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(utenteService.getAllUtenti(pageable));
    }

    @GetMapping("/prenotazione")
    public ResponseEntity<?> currentPrenotazione(@RequestParam int idPrenotazione){
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(idPrenotazione));
    };

    @PutMapping("/aggiornaPrenotazione")
    public ResponseEntity<?> updatePrenotazione(@Valid @RequestBody PrenotazioneRequest prenotazioneRequest,
                                                BindingResult bindingResult,@RequestParam int idPrenotazione){
        PrenotazioneDTO prenotazioneDTO = prenotazioneService.aggiornaPrenotazione(prenotazioneRequest, idPrenotazione);
        if(prenotazioneDTO != null){
            return ResponseEntity.ok(prenotazioneDTO);
        } else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/utente")
    public ResponseEntity<?> currentUtente(@RequestParam int idUtente){
        return ResponseEntity.ok(utenteService.getUtente(idUtente));
    }

    @PutMapping("/aggiornaUtente")
    public ResponseEntity<?> updateUtente(@Valid @RequestBody UtenteRequest utenteRequest,
    BindingResult bindingResult, @RequestParam int idUser){
        UtenteDTO utenteDTO = utenteService.aggiornaUtente(utenteRequest, idUser);
        if(utenteDTO != null){
            return ResponseEntity.ok(utenteDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    //prenota una nuova postazione
    @PostMapping("/prenotazione")
    public ResponseEntity<?> creaPrenotazione(@Valid @RequestBody PrenotazioneRequest prenotazioneRequest,
                                              BindingResult bindingResult,
                                              @RequestParam int idUser) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(e -> {
                log.info("Campo: " + e.getField());
                log.info("Valore: " + e.getRejectedValue());
                log.info("Codice: " + e.getCode());
                log.info("Messaggio: " + e.getDefaultMessage());
            });
            log.info(prenotazioneRequest.getNPostazione());
            log.info(String.valueOf(prenotazioneRequest.getDataInizio()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(",")));
        }

        //Utente utente = (Utente) session.getAttribute("utente");

        PrenotazioneDTO prenotazioneDTO = prenotazioneService.insertPrenotazione(prenotazioneRequest, idUser);
        if(prenotazioneDTO != null){
            return ResponseEntity.ok(prenotazioneDTO);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("errore utente in sessione");
        }


    }


    //endpoint per la ricerca con filtro
    @PostMapping("/searchUtenti")
    public ResponseEntity<?> searchUtente(@Valid @RequestBody UtenteFiltro utenteFiltro,
                                          BindingResult bindingResult,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size) throws FormErrorException {
        Pageable pageable = PageRequest.of(page, size);
        if (bindingResult.hasErrors()) {
            throw new FormErrorException();
        }

        return ResponseEntity.ok(utenteService.getUtentiByFilter(utenteFiltro, pageable));
    }

    @PostMapping("/searchPrenotazioniUtente")
    public ResponseEntity<?> searchUtentePrenotazioni(@Valid @RequestBody PrenotazioniFiltro prenotazioniFiltro,
                                                BindingResult bindingResult,
                                                @RequestParam int idUser,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size) throws FormErrorException {
        Pageable pageable = PageRequest.of(page, size);
        if (bindingResult.hasErrors()) {
            throw new FormErrorException();
        }

        return ResponseEntity.ok(prenotazioneService.getUtentePrenotazioniByFilter(idUser,prenotazioniFiltro, pageable));
    }

    @PostMapping("/searchPrenotazioni")
    public ResponseEntity<?> searchPrenotazioni(@Valid @RequestBody PrenotazioniFiltro prenotazioniFiltro,
                                                      BindingResult bindingResult,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size) throws FormErrorException {
        Pageable pageable = PageRequest.of(page, size);
        if (bindingResult.hasErrors()) {
            throw new FormErrorException();
        }

        return ResponseEntity.ok(prenotazioneService.getAllPrenotazioniByFilter(prenotazioniFiltro, pageable));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrenotazione(@PathVariable int id){
        prenotazioneService.deletePrenotazioneById(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("deleteUtente/{id}")
    public ResponseEntity<?> deleteUtente(@PathVariable int id){
        utenteService.deleteById(id);
        return ResponseEntity.ok().build();
    }



}
