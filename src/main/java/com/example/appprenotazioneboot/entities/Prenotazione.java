package com.example.appprenotazioneboot.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "prenotazione")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prenotazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "data_inizio")
    private LocalDateTime dataInizio;

    @Column(name = "data_fine")
    private LocalDateTime dataFine;

    @Column(name = "stato")
    private String stato;

    @Column(name="data_creazione")
    private LocalDateTime dataCreazione;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "user_key")
    private Utente utente;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_postazione", referencedColumnName = "id")
    private Postazione postazione;
}
