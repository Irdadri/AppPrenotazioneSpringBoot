package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.repository.PostazioneRepository;
import org.springframework.stereotype.Service;

@Service
public class PostazioneServiceImpl implements PostazioneService {
    private PostazioneRepository postazioneRepository;

    public PostazioneServiceImpl(PostazioneRepository postazioneRepository) {
        this.postazioneRepository = postazioneRepository;
    }
}
