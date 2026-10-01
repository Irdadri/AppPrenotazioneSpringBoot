package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.dto.KafkaMessage;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.*;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.openapitools.model.PrenotazioneDTO;
import org.openapitools.model.PrenotazioneRequest;
import org.openapitools.model.PrenotazioniFiltro;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;


import java.rmi.NoSuchObjectException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log
public class PrenotazioneServiceImpl implements PrenotazioneService {


    private final PostazioneRepository postazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ModelMapper modelMapper;
    private final PrenotazioneRepository repository;
    private final UtenteApi utenteApi;
    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final UtenteCacheService utenteCacheService;

    /*
    TODO: il metodo deve mandare un messaggio nel topic Kafka con un riepilogo prenotazione
     */
    @Override
    public PrenotazioneDTO insertPrenotazione(PrenotazioneRequest request, String userKey) {
        Utente utente = utenteRepository.findUtenteByUserKey(userKey);
        if (utente != null) {
            Prenotazione prenotazione = modelMapper.map(request, Prenotazione.class);
            prenotazione.setStato("prenotato");
            prenotazione.setPostazione(postazioneRepository.findPostazioneById(request.getNPostazione()));
            prenotazione.setUtente(utente);
            prenotazione.setDataFine(request.getDataInizio());
            prenotazione.setDataCreazione(LocalDateTime.now());
            try {
                repository.save(prenotazione);
                /*
                kafkaTemplate.send("notification", "prenotazione inserita con id: " + prenotazione.getId());

                kafkaTemplate.send("notification", "1");

                 */
                KafkaMessage message = new KafkaMessage();
                PrenotazioneDTO prenotazioneDTO = modelMapper.map(prenotazione, PrenotazioneDTO.class);
                UtenteHttp utenteHttp = utenteApi.getCurrentUtente(userKey);
                log.info(utenteHttp.getNome());

                /*
                message.setPrenotazioneDTO(modelMapper.map(prenotazione, PrenotazioneDTO.class));

                 */
                message.setTipoNotifica("EMAIL");
                Map<String, String> temp = message.getProperties();
                temp.put("citta", prenotazioneDTO.getCitta());
                temp.put("indirizzo", prenotazioneDTO.getIndirizzo());
                temp.put("nStanza", prenotazioneDTO.getNStanza());
                temp.put("nPostazione", String.valueOf(prenotazioneDTO.getNPostazione()));
                temp.put("dataInizio", String.valueOf(prenotazioneDTO.getDataInizio()));
                temp.put("dataFine", String.valueOf(prenotazioneDTO.getDataFine()));
                temp.put("nome utente", utenteHttp.getNome());
                temp.put("email", utenteHttp.getEmail());


                kafkaTemplate.send("notification", message);

                return prenotazioneDTO;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public PrenotazioneDTO getPrenotazioneById(int id) {

        Prenotazione prenotazione = repository.findPrenotazioneById(id);

        if (prenotazione == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata");
        }

        try {
            UtenteHttp utenteHttp =
             utenteCacheService.getUtenteHttp(prenotazione.getUtente().getUserKey());

            PrenotazioneDTO dto = modelMapper.map(prenotazione, PrenotazioneDTO.class);
            dto.setNomeUtente(utenteHttp.getNome());
            dto.setCognomeUtente(utenteHttp.getCognome());

            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato");
        }
    }

    @Override
    public PrenotazioneDTO aggiornaPrenotazione(PrenotazioneRequest prenotazioneRequest, int id){
        Prenotazione prenotazione = repository.findPrenotazioneById(id);
        if (prenotazione != null) {
            if (prenotazioneRequest.getNPostazione() != null) {
                prenotazione.setPostazione(postazioneRepository.findPostazioneById(prenotazioneRequest.getNPostazione()));
            }
            if (prenotazioneRequest.getDataInizio() != null) {
                prenotazione.setDataInizio(prenotazioneRequest.getDataInizio());
            }

            repository.save(prenotazione);
            return modelMapper.map(prenotazione, PrenotazioneDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata");
        }

    }

    @Override
    public void deletePrenotazioneById(int id){
        Prenotazione prenotazione = repository.findPrenotazioneById(id);
        if (prenotazione != null) {
            repository.delete(prenotazione);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata");
        }
    }


    @Override
    public Page<PrenotazioneDTO> getAllPrenotazioniWithPaging(String userKey, Pageable pageable) {
        UtenteHttp utente = utenteApi.getCurrentUtente(userKey);
        Utente _utente = utenteRepository.findUtenteByUserKey(utente.getUserKey());
        Page<Prenotazione> prenotazionePage = null;
        if (utente.getTipoUtente().equals(TipoUtenteEnum.user.name())) {
            prenotazionePage = repository.findPrenotazioneByUtente(_utente, pageable);

        } else if (utente.getTipoUtente().equals(TipoUtenteEnum.manager.name())) {
            prenotazionePage = repository.findAll(pageable);
        }

        return prenotazionePage.map(prenotazione -> {
            UtenteHttp temp = utenteApi.getCurrentUtente(prenotazione.getUtente().getUserKey());
            PrenotazioneDTO prenotazioneDTO = modelMapper.map(prenotazione, PrenotazioneDTO.class);
            prenotazioneDTO.setNomeUtente(temp.getNome());
            prenotazioneDTO.setCognomeUtente(temp.getCognome());
            return prenotazioneDTO;
        });

    }


    public static Specification<Prenotazione> dateBetween(LocalDateTime dataInizio, LocalDateTime dataFine) {
        return ((root, query, criteriaBuilder) -> {
            if (dataInizio != null && dataFine != null) {
                return criteriaBuilder.between(root.get("dataInizio"), dataInizio, dataFine);
            } else if (dataInizio != null && dataFine == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dataInizio"), dataInizio);
            } else if (dataInizio == null && dataFine != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("dataFine"), dataFine);
            }

            return null;
        });
    }

    /*
    //specification ha il metodo toPredicate(), costruisco il predicato con criteriaBuilder
    public static Specification<Prenotazione> hasEmail(String unique) {
        return ((root, query, criteriaBuilder) -> {
            Join<Prenotazione, Utente> prenotazioneUtenteJoin = root.join("utente");
            return criteriaBuilder.equal(prenotazioneUtenteJoin.get("email"), unique);
        });
    }

     */

    public static Specification<Prenotazione> fromUser(String userKey) {
        return ((root, query, criteriaBuilder) -> {
            Join<Prenotazione, Utente> prenotazioneUtenteJoin = root.join("utente");
            return criteriaBuilder.equal(prenotazioneUtenteJoin.get("userKey"), userKey);
        });
    }


    @Override
    public Page<PrenotazioneDTO> getAllPrenotazioniByFilter(PrenotazioniFiltro prenotazioniFiltro, Pageable pageable) {
        Specification<Prenotazione> specification = Specification.where(null);

        specification = specification.and(dateBetween(prenotazioniFiltro.getDataInizio(), prenotazioniFiltro.getDataFine()));


        if (prenotazioniFiltro.getEmail() != null) {
            UtenteHttp utenteHttp = utenteApi.getHttpUser(prenotazioniFiltro.getEmail());
            specification = specification.and(fromUser(utenteHttp.getUserKey()));
        }

        Page<PrenotazioneDTO> prenotazioneDTOS = repository.findAll(specification, pageable)
                .map(prenotazione -> {
                    UtenteHttp temp = utenteApi.getCurrentUtente(prenotazione.getUtente().getUserKey());
                    modelMapper.map(prenotazione, PrenotazioneDTO.class);
                    PrenotazioneDTO prenotazioneDTO = modelMapper.map(prenotazione, PrenotazioneDTO.class);
                    prenotazioneDTO.setNomeUtente(temp.getNome());
                    prenotazioneDTO.setCognomeUtente(temp.getCognome());
                    return prenotazioneDTO;
                });


        return prenotazioneDTOS;
    }

    @Override
    public Page<PrenotazioneDTO> getUtentePrenotazioniByFilter(String userKey, PrenotazioniFiltro prenotazioniFiltro, Pageable pageable) {
        Specification<Prenotazione> specification = Specification.where(null);


        specification = specification.and(dateBetween(prenotazioniFiltro.getDataInizio(), prenotazioniFiltro.getDataFine()));

        /*

        if (prenotazioniFiltro.getEmail() != null) {
            specification = specification.and(hasEmail(prenotazioniFiltro.getEmail()));
        }

         */

        if (userKey != null) {
            specification = specification.and(fromUser(userKey));
        }

        Page<PrenotazioneDTO> prenotazioneDTOS = repository.findAll(specification, pageable)
                .map(prenotazione -> {
                    UtenteHttp temp = utenteApi.getCurrentUtente(prenotazione.getUtente().getUserKey());
                    modelMapper.map(prenotazione, PrenotazioneDTO.class);
                    PrenotazioneDTO prenotazioneDTO = modelMapper.map(prenotazione, PrenotazioneDTO.class);
                    prenotazioneDTO.setNomeUtente(temp.getNome());
                    prenotazioneDTO.setCognomeUtente(temp.getCognome());
                    return prenotazioneDTO;
                });


        return prenotazioneDTOS;

    }


}
