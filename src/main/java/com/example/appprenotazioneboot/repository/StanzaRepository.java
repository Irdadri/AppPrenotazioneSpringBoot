package com.example.appprenotazioneboot.repository;


import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.Stanza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StanzaRepository extends JpaRepository<Stanza, Integer> {
    public List<Stanza> findBySede(Sede sede);
}
