package com.example.appprenotazioneboot.repository;

import com.example.appprenotazioneboot.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UtenteRepository extends JpaRepository<Utente, String>, JpaSpecificationExecutor<Utente> {
    public Utente findUtenteByUnique(String unique);
}
