package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.repository.SedeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeServiceImpl implements SedeService {
    private final SedeRepository sedeRepository;

    public SedeServiceImpl(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    @Override
    public List<Sede> getAllSedi() {
        return sedeRepository.findAll();
    }
}