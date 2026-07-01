package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.Stanza;
import com.example.appprenotazioneboot.repository.StanzaRepository;

import java.util.List;

public class StanzaServiceImpl implements StanzaService {
    private final StanzaRepository stanzaRepository;

    public StanzaServiceImpl(StanzaRepository stanzaRepository) {
        this.stanzaRepository = stanzaRepository;
    }


    @Override
    public List<Stanza> getStanzeBySede(Sede sede) {
        return stanzaRepository.findBySede(sede);
    }
}
