package com.example.appprenotazioneboot.repository;

import com.example.appprenotazioneboot.entities.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, Integer> {
    public Sede findSedeById(int id);

}