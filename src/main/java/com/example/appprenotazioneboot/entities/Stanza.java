package com.example.appprenotazioneboot.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "stanza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Stanza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "n_stanza")
    private String nStanza;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stanza", fetch = FetchType.EAGER)
    private List<Postazione> listaPostazioni;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_sede", referencedColumnName = "id")
    private Sede sede;
}
