package com.example.appprenotazioneboot.repository;

import com.example.appprenotazioneboot.entities.Postazione;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostazioneRepository extends JpaRepository<Postazione, Integer> {
    Postazione findPostazioneById(int id);
}
