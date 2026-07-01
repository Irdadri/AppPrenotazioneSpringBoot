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
@Table(name = "postazione")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Postazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "manutenzione")
    private boolean manutenzione;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_stanza", referencedColumnName = "id")
    private Stanza stanza;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postazione")
    private List<Prenotazione> listaPrenotazioni;
}
