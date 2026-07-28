package com.example.appprenotazioneboot.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "utente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Utente {
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "telefono")
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_utente")
    private TipoUtenteEnum tipoUtente;
     */

    @Id
    @Column(name = "user_key")
    private String userKey;

    @ManyToOne
    @JoinColumn(name = "id_sede", referencedColumnName = "id")
    private Sede sede;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "utente")
    private List<Prenotazione> listaPrenotazioni;

}
