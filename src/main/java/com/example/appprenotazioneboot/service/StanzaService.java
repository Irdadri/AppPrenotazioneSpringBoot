package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.Stanza;

import java.util.List;

public interface StanzaService {
    public List<Stanza> getStanzeBySede(Sede sede);
}
