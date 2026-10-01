package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.entities.Postazione;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.PostazioneRepository;
import com.example.appprenotazioneboot.repository.PrenotazioneRepository;
import com.example.appprenotazioneboot.repository.SedeRepository;
import com.example.appprenotazioneboot.repository.UtenteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.openapitools.model.PrenotazioneDTO;
import org.openapitools.model.PrenotazioneRequest;
import org.openapitools.model.PrenotazioniFiltro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class PrenotazioneServiceTest {

    @Autowired
    PrenotazioneService prenotazioneService;

    @MockBean
    UtenteApi utenteApi;
    @MockBean
    UtenteCacheService utenteCacheService;

    UtenteHttp utenteManager;
    UtenteHttp utenteUser;

    @Autowired
    private PostazioneRepository postazioneRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    @Spy
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;

    private Prenotazione prenotazioneUtente;
    private Prenotazione prenotazioneManager;
    private PrenotazioneRequest prenotazioneRequest;

    @BeforeEach
    void setup() {
        utenteRepository.deleteAll();
        postazioneRepository.deleteAll();
        prenotazioneRepository.deleteAll();

        Utente manager = new Utente();
        manager.setUserKey("d78d1b8b-7439-4b07-9488-44f0f08a6293");
        utenteRepository.save(manager);

        Utente utente = new Utente();
        utente.setUserKey("4fc9beed-5244-4f60-90c5-5239a799b710");
        utenteRepository.save(utente);

        Postazione postazione = new Postazione();
        postazioneRepository.save(postazione);

        Postazione postazione2 = new Postazione();
        postazioneRepository.save(postazione2);

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setUtente(manager);
        prenotazione.setPostazione(postazione);
        this.prenotazioneManager = prenotazione;
        prenotazioneRepository.save(prenotazione);

        Prenotazione prenotazione_2 = new Prenotazione();
        prenotazione_2.setUtente(utente);
        prenotazione_2.setPostazione(postazione);
        this.prenotazioneUtente = prenotazione_2;
        prenotazioneRepository.save(prenotazione_2);

        this.utenteManager = new UtenteHttp();
        this.utenteManager.setNome("adriana");
        this.utenteManager.setCognome("sciarratta");
        this.utenteManager.setEmail("adriana@adriana");
        this.utenteManager.setTipoUtente("manager");
        this.utenteManager.setTelefono("123456789");
        this.utenteManager.setUserKey("d78d1b8b-7439-4b07-9488-44f0f08a6293");

        this.utenteUser = new UtenteHttp();
        this.utenteUser.setNome("mario");
        this.utenteUser.setCognome("mario");
        this.utenteUser.setEmail("mario@mario");
        this.utenteUser.setTipoUtente("user");
        this.utenteUser.setTelefono("123456789");
        this.utenteUser.setUserKey("4fc9beed-5244-4f60-90c5-5239a799b710");

        PrenotazioneRequest prenotazioneRequest = new PrenotazioneRequest();
        prenotazioneRequest.setNPostazione(postazione2.getId());
        prenotazioneRequest.setDataInizio(LocalDateTime.now());
        this.prenotazioneRequest = prenotazioneRequest;


    }


    @Test
    @DisplayName("insertPrenotazione")
    public void givenRequestAndUserKey_insertPrenotazione() throws JsonProcessingException {
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";


        PrenotazioneDTO prenotazioneDTO = prenotazioneService.insertPrenotazione(prenotazioneRequest, userKey);

        assertNotNull(prenotazioneDTO);
        assertEquals(prenotazioneRequest.getNPostazione(), prenotazioneDTO.getNPostazione());
        assertEquals(prenotazioneRequest.getDataInizio(), prenotazioneDTO.getDataInizio());
        assertEquals(prenotazioneRequest.getDataInizio(), prenotazioneDTO.getDataFine());

    }

    @Test
    @DisplayName("insertPrenotazione not found")
    public void givenRequestAndUserKey_insertPrenotazione_throwsNotFound() throws JsonProcessingException {
        String userKey = "utente-non-esistente";

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> prenotazioneService.insertPrenotazione(prenotazioneRequest, userKey)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

    }

    @Test
    @DisplayName("get prenotazione")
    public void givenId_getPrenotazioneById() {

        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        PrenotazioneDTO prenotazioneDTO = prenotazioneService.getPrenotazioneById(prenotazioneManager.getId());

        assertNotNull(prenotazioneDTO);
        assertEquals(prenotazioneDTO.getNPostazione(), prenotazioneManager.getPostazione().getId());
        assertEquals(prenotazioneDTO.getId(), prenotazioneManager.getId());
    }

    @Test
    @DisplayName("get prenotazione not found")
    public void givenId_getPrenotazioneById_notFound() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> prenotazioneService.getPrenotazioneById(0)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    @DisplayName("get prenotazione utente not found")
    public void givenId_getPrenotazioneById_UtenteNotFound(){

        given(utenteApi.getCurrentUtente(utenteManager.getUserKey()))
                 .willThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Utente non trovato",
                        HttpHeaders.EMPTY,
                        null,
                        StandardCharsets.UTF_8
                ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> prenotazioneService.getPrenotazioneById(prenotazioneManager.getId())
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    @DisplayName("update prenotazione")
    public void givenIdAndRequest_aggiornaPrenotazione() {

        PrenotazioneDTO prenotazioneDTO = prenotazioneService.aggiornaPrenotazione(prenotazioneRequest, prenotazioneManager.getId());
        assertNotNull(prenotazioneDTO);
        assertEquals(prenotazioneRequest.getNPostazione(), prenotazioneDTO.getNPostazione());
        assertEquals(prenotazioneRequest.getDataInizio(), prenotazioneDTO.getDataInizio());
    }

    @Test
    @DisplayName("update prenotazione not found")
    public void givenIdAndRequest_aggiornaPrenotazione_prenotazioneNotFound() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () ->
                        prenotazioneService.aggiornaPrenotazione(prenotazioneRequest, 0)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

    }


    @Test
    @DisplayName("get all prenotazioni")
    public void givenUserKey_getAllPrenotazioniWithPaging() {
        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);

        Pageable pageable = PageRequest.of(0, 5);

        Page<PrenotazioneDTO> prenotazioneDTOPage = prenotazioneService.getAllPrenotazioniWithPaging("d78d1b8b-7439-4b07-9488-44f0f08a6293", pageable);

        assertNotNull(prenotazioneDTOPage);
    }

    @Test
    @DisplayName("get filtered prenotazioni")
    public void givenFilter_getAllPrenotazioniByFilter() {
        PrenotazioniFiltro prenotazioniFiltro = new PrenotazioniFiltro();
        prenotazioniFiltro.setEmail("adriana@adriana");
        Pageable pageable = PageRequest.of(0, 5);

        given(utenteApi.getHttpUser("adriana@adriana"))
                .willReturn(utenteManager);
        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);


        Page<PrenotazioneDTO> prenotazioneDTOS = prenotazioneService.getAllPrenotazioniByFilter(prenotazioniFiltro, pageable);

        assertNotNull(prenotazioneDTOS);
    }

    @Test
    @DisplayName("get filtered prenotazioni utente")
    public void givenFilter_getAllPrenotazioniUtenteByFilter() {
        PrenotazioniFiltro prenotazioniFiltro = new PrenotazioniFiltro();
        Pageable pageable = PageRequest.of(0, 5);
        /*
        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);
         */
        String userKey = "4fc9beed-5244-4f60-90c5-5239a799b710";
        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);


        Page<PrenotazioneDTO> prenotazioneDTOS = prenotazioneService.getUtentePrenotazioniByFilter(userKey, prenotazioniFiltro, pageable);

        assertNotNull(prenotazioneDTOS);
    }

    @Test
    @DisplayName("delete prenotazione")
    public void givenId_deletePrenotazioneById() {
        prenotazioneService.deletePrenotazioneById(prenotazioneManager.getId());
    }

    @Test
    @DisplayName("delete prenotazione not found")
    public void givenId_deletePrenotazioneById_notFound() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> prenotazioneService.deletePrenotazioneById(0)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());


    }


}
