package com.example.appprenotazioneboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//modello per lista utenti
public class UtenteDTO {

    private String nome;

    private String cognome;

    private String email;

    private String telefono;

    private String tipoUtente;

    private String paese;

    private String citta;

    private String regione;

    private String indirizzo;
}
