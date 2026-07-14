package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.dto.PrenotazioneDTO;
import com.example.appprenotazioneboot.dto.PrenotazioneRequest;
import com.example.appprenotazioneboot.dto.PrenotazioniFiltro;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final SedeRepository sedeRepository;
    private final StanzaRepository stanzaRepository;

    private final PostazioneRepository postazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ModelMapper modelMapper;
    private final PrenotazioneRepository repository;


//    @Override
//    public List<PrenotazioneDTO> getAllPrenotazioni() {
//        List<Prenotazione> listaPrenotazioni = repository.findAll();
//        return listaPrenotazioni.stream()
//                .map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class))
//                .collect(Collectors.toList());
//    }

//    @Override
//    public List<PrenotazioneDTO> getPrenotazioniUtente(Utente utente) {
//        List<Prenotazione> listaPrenotazioni = repository.findPrenotazioneByUtente(utente);
//        return listaPrenotazioni.stream()
//                .map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class))
//                .collect(Collectors.toList());
//    }

    @Override
    public PrenotazioneDTO insertPrenotazione(PrenotazioneRequest request, int idUser) {
        Utente utente = utenteRepository.findUtenteById(idUser);
        if(utente != null) {
            Prenotazione prenotazione = modelMapper.map(request, Prenotazione.class);
            prenotazione.setStato("prenotato");
            prenotazione.setPostazione(postazioneRepository.findPostazioneById(Integer.parseInt(request.getNPostazione())));
            prenotazione.setUtente(utente);
            prenotazione.setDataFine(request.getDataInizio());
            repository.save(prenotazione);
            return modelMapper.map(prenotazione, PrenotazioneDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public PrenotazioneDTO getPrenotazioneById(int id) {
        return modelMapper.map(repository.findPrenotazioneById(id), PrenotazioneDTO.class);
    }

    @Override
    public PrenotazioneDTO aggiornaPrenotazione(PrenotazioneRequest prenotazioneRequest, int id) {
        Prenotazione prenotazione = repository.findPrenotazioneById(id);
        if(prenotazione != null) {
            if (prenotazioneRequest.getNPostazione() != null) {
                prenotazione.setPostazione(postazioneRepository.findPostazioneById(Integer.parseInt(prenotazioneRequest.getNPostazione())));
            }
            if (prenotazioneRequest.getDataInizio() != null) {
                prenotazione.setDataInizio(prenotazioneRequest.getDataInizio());
            }

            repository.save(prenotazione);
            return modelMapper.map(prenotazione, PrenotazioneDTO.class);
        }

        return null;
    }

    @Override
    public void deletePrenotazioneById(int id) {
        Prenotazione prenotazione = repository.findPrenotazioneById(id);
        if(prenotazione != null){
            repository.delete(prenotazione);
        }
    }

    @Override
    public Page<PrenotazioneDTO> getAllPrenotazioniWithPaging(int id, Pageable pageable) {
        Utente utente = utenteRepository.findUtenteById(id);
        if(utente.getTipoUtente().name().equals(TipoUtenteEnum.user.name())){
            Page<Prenotazione> prenotazionePage = repository.findPrenotazioneByUtente(utente, pageable);
            return prenotazionePage.map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class));
        } else if(utente.getTipoUtente().name().equals(TipoUtenteEnum.manager.name())){
            Page<Prenotazione> prenotazionePage = repository.findAll(pageable);
            return prenotazionePage.map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class));
        }

        return null;
    }

    @Override
    public List<PrenotazioneDTO> getPrenotazioni(String idUtente) {
        int id = Integer.parseInt(idUtente);
        Utente utente = utenteRepository.findUtenteById(id);

        if(utente != null){
            if(utente.getTipoUtente().name().equals(TipoUtenteEnum.user.name())){
               return repository.findPrenotazioneByUtente(utente).stream()
                       .map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class))
                       .collect(Collectors.toList());
            } else if(utente.getTipoUtente().name().equals(TipoUtenteEnum.manager.name())){
                return  repository.findAll().stream()
                        .map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class))
                        .collect(Collectors.toList());
            }
        } else {
            return null;
        }

        return List.of();
    }



    public static Specification<Prenotazione> dateBetween(LocalDateTime dataInizio, LocalDateTime dataFine){
        return ((root, query, criteriaBuilder) -> {
            if(dataInizio != null && dataFine != null){
                return criteriaBuilder.between(root.get("dataInizio"), dataInizio, dataFine);
            }else if(dataInizio != null && dataFine == null){
                return  criteriaBuilder.greaterThanOrEqualTo(root.get("dataInizio"), dataInizio);
            } else if(dataInizio == null && dataFine != null){
                return  criteriaBuilder.lessThanOrEqualTo(root.get("dataFine"), dataFine);
            }

            return null;
        });
    }

    //specification ha il metodo toPredicate(), costruisco il predicato con criteriaBuilder
    public static Specification<Prenotazione> hasEmail(String email){
        return ((root, query, criteriaBuilder) -> {
            Join<Prenotazione, Utente> prenotazioneUtenteJoin = root.join("utente");
            return criteriaBuilder.equal(prenotazioneUtenteJoin.get("email"), email);
        });
    }

    public static Specification<Prenotazione> fromUser(int idUser){
        return((root, query, criteriaBuilder) -> {
            Join<Prenotazione, Utente> prenotazioneUtenteJoin = root.join("utente");
            return criteriaBuilder.equal(prenotazioneUtenteJoin.get("id"), idUser);
        });
    }


    @Override
    public Page<PrenotazioneDTO> getAllPrenotazioniByFilter(PrenotazioniFiltro prenotazioniFiltro, Pageable pageable) {
        Specification<Prenotazione> specification = Specification.where(null);

        specification = specification.and(dateBetween(prenotazioniFiltro.getDataInizio(), prenotazioniFiltro.getDataFine()));


        if(prenotazioniFiltro.getEmail() != null){
            specification = specification.and(hasEmail(prenotazioniFiltro.getEmail()));
        }

        return repository.findAll(specification, pageable)
                .map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class));
    }

    @Override
    public Page<PrenotazioneDTO> getUtentePrenotazioniByFilter(int idUser, PrenotazioniFiltro prenotazioniFiltro, Pageable pageable) {
        Specification<Prenotazione> specification = Specification.where(null);


        specification = specification.and(dateBetween(prenotazioniFiltro.getDataInizio(), prenotazioniFiltro.getDataFine()));


        if(prenotazioniFiltro.getEmail() != null){
            specification = specification.and(hasEmail(prenotazioniFiltro.getEmail()));
        }

        if(idUser != 0){
            specification = specification.and(fromUser(idUser));
        }

        return repository.findAll(specification, pageable)
                .map(prenotazione -> modelMapper.map(prenotazione, PrenotazioneDTO.class));

    }


}
