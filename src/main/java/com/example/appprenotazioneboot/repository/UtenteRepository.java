package com.example.appprenotazioneboot.repository;

import com.example.appprenotazioneboot.entities.Utente;
import org.openapitools.client.model.UtenteHttp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UtenteRepository extends JpaRepository<Utente, Integer>, JpaSpecificationExecutor<Utente> {
    public Utente findUtenteByUserKey(String userKey);
}
