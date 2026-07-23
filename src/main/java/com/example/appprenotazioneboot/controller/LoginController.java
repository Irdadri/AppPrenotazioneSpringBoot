//package com.example.appprenotazioneboot.controller;
//
//import com.example.appprenotazioneboot.dto.LoginRequest;
//import com.example.appprenotazioneboot.entities.Utente;
//import com.example.appprenotazioneboot.service.UtenteService;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import lombok.extern.java.Log;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@Log
//@CrossOrigin(origins = "http://localhost:4200")
//public class LoginController {
//
//    private UtenteService utenteService;
//    private final HttpSession session;
//
//    public LoginController(UtenteService utenteService, HttpSession session) {
//        this.utenteService = utenteService;
//        this.session = session;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequest loginRequest,
//                                        BindingResult result){
//        if(result.hasErrors()){
//            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            log.info("errore");
//            return ResponseEntity.badRequest().build();
//        }
//
//        Utente utente = utenteService.loginUtente(loginRequest.getEmail(), loginRequest.getPassword());
//
//        if(utente != null){
//            //return ResponseEntity.ok("Hello World!");
//            session.setAttribute("utente", utente);
//            return new ResponseEntity<Utente>(utente, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("email o password errati", HttpStatus.BAD_REQUEST);
//        }
//    }
//}
