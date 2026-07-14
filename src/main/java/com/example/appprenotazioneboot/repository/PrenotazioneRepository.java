package com.example.appprenotazioneboot.repository;

import com.example.appprenotazioneboot.dto.PrenotazioneDTO;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
// PagingAndSortingRepository<Prenotazione, Integer> è già estesa da jpaRepository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer>, JpaSpecificationExecutor<Prenotazione> {

    public List<Prenotazione> findPrenotazioneByUtente(Utente utente);

    Page<Prenotazione> findPrenotazioneByUtente(Utente utente, Pageable pageable);
    Prenotazione findPrenotazioneById(int id);
}
