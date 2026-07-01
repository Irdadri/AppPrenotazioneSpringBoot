package com.example.appprenotazioneboot.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "sede")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "paese")
    private String paese;

    @Column(name = "citta")
    private String citta;

    @Column(name = "regione")
    private String regione;

    @Column(name = "indirizzo")
    private String indirizzo;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sede")
    private List<Stanza> listStanze;
}
