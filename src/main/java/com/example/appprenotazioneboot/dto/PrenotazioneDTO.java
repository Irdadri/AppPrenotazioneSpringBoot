//package com.example.appprenotazioneboot.dto;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
////modello per lista prenotazioni
//public class PrenotazioneDTO {
//    private int id;
//    private String nomeUtente;
//    private String cognomeUtente;
//    private String citta;
//    private String indirizzo;
//    private String nStanza;
//    private int nPostazione;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime dataInizio;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime dataFine;
//
//    private String stato;
//
//}
