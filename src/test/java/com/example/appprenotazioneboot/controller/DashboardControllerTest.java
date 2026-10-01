package com.example.appprenotazioneboot.controller;

import com.example.appprenotazioneboot.entities.Postazione;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.PostazioneRepository;
import com.example.appprenotazioneboot.repository.PrenotazioneRepository;
import com.example.appprenotazioneboot.repository.UtenteRepository;
import com.example.appprenotazioneboot.service.PrenotazioneService;
import com.example.appprenotazioneboot.service.UtenteCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.openapitools.model.PrenotazioneRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class DashboardControllerTest {

    @Autowired
    private MockMvc mvc;

    /*
    @MockBean
    private PrenotazioneService prenotazioneService;

     */

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostazioneRepository postazioneRepository;


    @MockBean
    UtenteApi utenteApi;
    UtenteHttp utenteManager;
    UtenteHttp utenteUser;

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


    @WithMockUser
    @Test
    @DisplayName("crea prenotazione")
    public void givenUserKeyAndRequest_creaPrenotazione_ThenStatus200() throws Exception {
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";

        String json = objectMapper.writeValueAsString(prenotazioneRequest);


        //given(prenotazioneService.insertPrenotazione(prenotazioneRequest, userKey)).willReturn(prenotazione);
        mvc.perform(post("/dashboard/prenotazione")
                        .param("userKey", userKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

    }
    @WithMockUser
    @Test
    @DisplayName("crea prenotazione utente non trovato")
    public void givenUserKeyAndRequest_creaPrenotazione_ThenStatus404() throws Exception{
        String userKey = "utente-non-esistente";

        String json = objectMapper.writeValueAsString(prenotazioneRequest);


        //given(prenotazioneService.insertPrenotazione(prenotazioneRequest, userKey)).willReturn(prenotazione);
        mvc.perform(post("/dashboard/prenotazione")
                        .param("userKey", userKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }



    @WithMockUser
    @Test
    @DisplayName("current prenotazione")
    public void givenId_givesCurrentPrenotazione_thenStatus200() throws Exception {
        //given
        int id = prenotazioneManager.getId();

        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);


        mvc.perform(get("/dashboard/prenotazione")
                        .param("idPrenotazione", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id));

    }
    @WithMockUser
    @Test
    @DisplayName("current prenotazione not found")
    public void givenId_givesCurrentPrenotazione_thenStatus404() throws Exception {
        //given
        int id = 0;

        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);
        mvc.perform(get("/dashboard/prenotazione")
                        .param("idPrenotazione", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @WithMockUser
    @Test
    @DisplayName("update prenotazione")
    public void givenIdAndRequest_updatePrenotazione_thenStatus200() throws Exception {
        int id = prenotazioneUtente.getId();

        /*
        String json = """
                {
                  "nPostazione": 1,
                  "dataInizio": "2026-07-31T07:45:58.238Z"
                }
                """;
        //prenotazioneRequest.setDataInizio(new LocalDateTime());

         */

        String json = objectMapper.writeValueAsString(prenotazioneRequest);
        mvc.perform(put("/dashboard/aggiornaPrenotazione")
                        .param("idPrenotazione", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

    }

    @WithMockUser
    @Test
    @DisplayName("update prenotazione not found")
    public void givenIdAndRequest_updatePrenotazione_thenStatus404() throws Exception {
        int id = 10;

        /*
        String json = """
                {
                  "nPostazione": 1,
                  "dataInizio": "2026-07-31T07:45:58.238Z"
                }
                """;
        //prenotazioneRequest.setDataInizio(new LocalDateTime());

         */

        String json = objectMapper.writeValueAsString(prenotazioneRequest);

        mvc.perform(put("/dashboard/aggiornaPrenotazione")
                        .param("idPrenotazione", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

    }


    @WithMockUser
    @Test
    @DisplayName("lista delle prenotazioni")
    public void givenUserKey_givesListaPrenotazione_andStatus200() throws Exception{

        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";

        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);

        mvc.perform(get("/dashboard/")
                        .param("userKey", userKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }


    @WithMockUser(authorities = "ROLE_manager")
    @Test
    @DisplayName("ricerca prenotazione manager")
    public void givenFilter_givesFilteredPrenotazioniManager() throws Exception{
        String json = """
                {
                  "email": "adriana@adriana"
                
                }
                """;

        given(utenteApi.getHttpUser("adriana@adriana"))
                .willReturn(utenteManager);
        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);

        mvc.perform(post("/dashboard/searchPrenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        ;

    }

    @WithMockUser(authorities = "ROLE_user")
    @Test
    @DisplayName("ricerca prenotazioni dell'utente")
    public void givenFilterAndUserKey_givesFilteredPrenotazioniUser() throws  Exception{
        String userKey = "4fc9beed-5244-4f60-90c5-5239a799b710";
        String json = """
                {
                  "dataInizio": ""
                
                }
                """;
        given(utenteApi.getHttpUser("mario@mario"))
                .willReturn(utenteUser);
        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);


        mvc.perform(post("/dashboard/searchPrenotazioniUtente")
                        .param("userKey", userKey)
                        .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        ;

    }

    @WithMockUser
    @Test
    @DisplayName("elimina prenotazione")
    public void givenId_deletesPrenotazione_andStatus200() throws Exception{
        int id = 1;

        mvc.perform(delete("/dashboard/delete/{id}", id))
                .andExpect(status().isOk());

    }

    @WithMockUser
    @Test
    @DisplayName("elimina prenotazione")
    public void givenId_deletesPrenotazione_andStatus404() throws Exception{
        int id = 10;

        mvc.perform(delete("/dashboard/delete/{id}", id))
                .andExpect(status().isNotFound());

    }



}
