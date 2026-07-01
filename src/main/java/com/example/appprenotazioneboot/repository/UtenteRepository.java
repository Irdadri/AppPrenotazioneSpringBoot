package com.example.appprenotazioneboot.repository;

import com.example.appprenotazioneboot.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UtenteRepository extends JpaRepository<Utente, Integer>, JpaSpecificationExecutor<Utente> {
    public Utente findByEmail(String email);
    public Utente findUtenteById(int id);
}
