package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.config.ModelMapperConfig;
import com.example.appprenotazioneboot.entities.Postazione;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.PostazioneRepository;
import com.example.appprenotazioneboot.repository.PrenotazioneRepository;
import com.example.appprenotazioneboot.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.openapitools.model.PrenotazioneDTO;
import org.openapitools.model.PrenotazioneRequest;
import org.openapitools.model.PrenotazioniFiltro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PrenotazioneServiceUnitTest {

    @Mock
    PrenotazioneRepository repository;
    @Mock
    UtenteRepository utenteRepository;
    @Mock
    PostazioneRepository postazioneRepository;
    @Mock
    KafkaTemplate kafkaTemplate;


    private ModelMapper modelMapper;

    private PrenotazioneServiceImpl prenotazioneService;

    @Mock
    UtenteApi utenteApi;
//    @Mock
//    UtenteCacheService utenteCacheService;



    //object mocks

    UtenteHttp utenteManager;
    UtenteHttp utenteUser;
    Utente user;
    Utente manager;
    private Prenotazione prenotazioneUtente;
    private Prenotazione prenotazioneManager;
    private PrenotazioneRequest prenotazioneRequest;
    private Postazione postazione;
    private Postazione postazione2;
    private Postazione postazioneRequest;


    @BeforeEach
    void setUp() {

        //manual injection
        //necessaria per modelmapper
        modelMapper = new ModelMapperConfig().modelMapper();

        prenotazioneService = new PrenotazioneServiceImpl(
                postazioneRepository,
                utenteRepository,
                modelMapper,
                repository,
                utenteApi,
                kafkaTemplate
//                ,
//                utenteCacheServices
        );


        //inizializzazione dei mock

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

        String managerKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        Utente manager = new Utente();
        manager.setUserKey(managerKey);
        this.manager = manager;

        String userKey = "4fc9beed-5244-4f60-90c5-5239a799b710";
        Utente user = new Utente();
        user.setUserKey(userKey);
        this.user = user;

        Postazione postazione = new Postazione();
        postazione.setId(1);
        this.postazione = postazione;
        Postazione postazione2 = new Postazione();
        postazione2.setId(2);
        this.postazione2 = postazione2;
        Postazione postazioneRequest = new Postazione();
        postazioneRequest.setId(10);
        this.postazioneRequest = postazioneRequest;



        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setId(1);
        prenotazione.setUtente(manager);
        prenotazione.setPostazione(postazione);
        this.prenotazioneManager = prenotazione;

        Prenotazione prenotazione_2 = new Prenotazione();
        prenotazione_2.setId(2);
        prenotazione_2.setUtente(user);
        prenotazione_2.setPostazione(postazione);
        this.prenotazioneUtente = prenotazione_2;

        PrenotazioneRequest prenotazioneRequest = new PrenotazioneRequest();
        prenotazioneRequest.setNPostazione(10);
        prenotazioneRequest.setDataInizio(LocalDateTime.now());
        this.prenotazioneRequest = prenotazioneRequest;

    }


    @Test
    @DisplayName("insert prenotazione")
    public void givenRequestAndUserId_insertPrenotazione(){
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        given(utenteRepository.findUtenteByUserKey(userKey))
                .willReturn(manager);


        given(postazioneRepository.findPostazioneById(postazione.getId()))
                .willReturn(postazione);

        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setNPostazione(postazione.getId());
        request.setDataInizio(LocalDateTime.now());


        PrenotazioneDTO prenotazioneDTO = prenotazioneService.insertPrenotazione(request, userKey);
        verify(repository).save(Mockito.any(Prenotazione.class));
        assertNotNull(prenotazioneDTO);
        assertEquals(request.getNPostazione(), prenotazioneDTO.getNPostazione());
        assertEquals(request.getDataInizio(), prenotazioneDTO.getDataInizio());
        assertEquals(request.getDataInizio(), prenotazioneDTO.getDataFine());
    }

    @Test
    @DisplayName("insert - utente non trovato")
    public void givenRequestAndUserId_insertPrenotazione_notFound(){
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        given(utenteRepository.findUtenteByUserKey(userKey))
                .willReturn(null);
        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setNPostazione(postazione.getId());
        request.setDataInizio(LocalDateTime.now());


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prenotazioneService.insertPrenotazione(request, userKey)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

    }

    @Test
    @DisplayName("insert internal server error")
    public void givenRequestAndUserId_insertPrenotazione_InternalServerError(){
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        given(utenteRepository.findUtenteByUserKey(userKey))
                .willReturn(manager);


        given(postazioneRepository.findPostazioneById(postazione.getId()))
                .willReturn(postazione);

        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setNPostazione(postazione.getId());
        request.setDataInizio(LocalDateTime.now());

        Prenotazione prenotazione = new Prenotazione();

        given(repository.save(prenotazione)).willThrow( new RuntimeException());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prenotazioneService.insertPrenotazione(request, userKey)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    @DisplayName("prenotazione by id")
    public void givenId_getPrenotazioneById(){
        int id = 1;
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        given(repository.findPrenotazioneById(id)).willReturn(prenotazioneManager);
        given(utenteApi.getCurrentUtente(userKey)).willReturn(utenteManager);

        PrenotazioneDTO prenotazioneDTO = prenotazioneService.getPrenotazioneById(id);
        assertNotNull(prenotazioneDTO);
        assertEquals(utenteManager.getNome(), prenotazioneDTO.getNomeUtente());
    }

    @Test
    @DisplayName("prenotazione by id, prenotazione not found")
    public void givenId_getPrenotazioneById_prenotazioneNotFound(){
        int id = 1;
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        given(repository.findPrenotazioneById(id)).willReturn(null);


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prenotazioneService.getPrenotazioneById(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("prenotazione by id, utente not found")
    public void givenId_getPrenotazioneById_utenteNotFound(){
        int id = 1;
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";
        given(repository.findPrenotazioneById(id)).willReturn(prenotazioneManager);
        given(utenteApi.getCurrentUtente(userKey))
                .willThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Utente non trovato",
                        HttpHeaders.EMPTY,
                        null,
                        StandardCharsets.UTF_8
                ));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prenotazioneService.getPrenotazioneById(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("aggiorna prenotazione")
    public void givenRequestAndId_aggiornaPrenotazione(){
        int id = 1;
        //prenotazione manager ha postazione con id 1
        //lo cambio in id 10
        given(repository.findPrenotazioneById(id)).willReturn(prenotazioneManager);
        given(postazioneRepository.findPostazioneById(prenotazioneRequest.getNPostazione())).willReturn(postazioneRequest);

        PrenotazioneDTO prenotazioneDTO = prenotazioneService.aggiornaPrenotazione(prenotazioneRequest, id);
        verify(repository).save(prenotazioneManager);
        assertNotNull(prenotazioneDTO);
        assertEquals(prenotazioneDTO.getNPostazione(), prenotazioneRequest.getNPostazione());
    }

    @Test
    @DisplayName("aggiorna prenotazione, prenotazione not found")
    public void givenRequestAndId_aggiornaPrenotazione_prenotazioneNotFound(){
        int id = 1;
        //prenotazione manager ha postazione con id 1
        //lo cambio in id 10
        given(repository.findPrenotazioneById(id)).willReturn(null);

       ResponseStatusException exception = assertThrows(ResponseStatusException.class,
               () -> prenotazioneService.aggiornaPrenotazione(prenotazioneRequest, id)
       );

       assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("delete prenotazione by id")
    public void givenId_deletePrenotazioneById(){
        int id = 1;
        given(repository.findPrenotazioneById(id)).willReturn(prenotazioneManager);
        prenotazioneService.deletePrenotazioneById(id);

        verify(repository).delete(prenotazioneManager);
    }

    @Test
    @DisplayName("delete prenotazione not found")
    public void givenId_deletePrenotazioneById_notFound(){
        int id = 1;
        given(repository.findPrenotazioneById(id)).willReturn(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prenotazioneService.deletePrenotazioneById(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

    }


    @Test
    @DisplayName("get all prenotazioni paginate")
    public void givenUserKey_givesAllPrenotazioniWithPaging(){
        String userKey = "d78d1b8b-7439-4b07-9488-44f0f08a6293";

        Pageable pageable = PageRequest.of(0, 5);

        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);

        given(utenteRepository.findUtenteByUserKey(userKey)).willReturn(manager);

        List<Prenotazione> content = new ArrayList<>();
        content.add(prenotazioneManager);
        content.add(prenotazioneUtente);
        Page<Prenotazione> prenotazionePage = new PageImpl<>(content);

        given(repository.findAll(pageable)).willReturn(prenotazionePage);

        Page<PrenotazioneDTO> prenotazioneDTOS = prenotazioneService.getAllPrenotazioniWithPaging(userKey, pageable);
        assertNotNull(prenotazioneDTOS);
        assertEquals(prenotazioneDTOS.getTotalElements(), content.size());
    }


    @Test
    @DisplayName("prenotazioni filtrate")
    public void givenFiltro_getAllPrenotazioniByFilter(){
        PrenotazioniFiltro filtro = new PrenotazioniFiltro();
        filtro.setEmail("adriana@adriana");
        given(utenteApi.getHttpUser(utenteManager.getEmail())).willReturn(utenteManager);
        Pageable pageable = PageRequest.of(0, 5);

        List<Prenotazione> content = new ArrayList<>();
        content.add(prenotazioneManager);
        Page<Prenotazione> prenotazionePage = new PageImpl<>(content);

        given(repository.findAll(Mockito.any(Specification.class),  Mockito.eq(pageable))).willReturn(prenotazionePage);
        given(utenteApi.getCurrentUtente("d78d1b8b-7439-4b07-9488-44f0f08a6293"))
                .willReturn(utenteManager);

        Page<PrenotazioneDTO> prenotazioneDTOS = prenotazioneService.getAllPrenotazioniByFilter(filtro, pageable);

        assertNotNull(prenotazioneDTOS);
        List<PrenotazioneDTO> prenotazioneDTOS1 = prenotazioneDTOS.getContent();
        prenotazioneDTOS1.forEach( prenotazione -> assertEquals(utenteManager.getNome(), prenotazione.getNomeUtente()));
    }

    @Test
    @DisplayName("utente prenotazioni by filter")
    public void givenFiltroAndUserKey_getUtentePrenotazioniByFilter(){
        PrenotazioniFiltro filtro = new PrenotazioniFiltro();
        String userKey = "4fc9beed-5244-4f60-90c5-5239a799b710";

        Pageable pageable = PageRequest.of(0, 5);

        List<Prenotazione> content = new ArrayList<>();
        content.add(prenotazioneUtente);
        Page<Prenotazione> prenotazionePage = new PageImpl<>(content);

        given(repository.findAll(Mockito.any(Specification.class),  Mockito.eq(pageable))).willReturn(prenotazionePage);
        given(utenteApi.getCurrentUtente("4fc9beed-5244-4f60-90c5-5239a799b710"))
                .willReturn(utenteUser);

        Page<PrenotazioneDTO> prenotazioneDTOS = prenotazioneService.getUtentePrenotazioniByFilter(userKey, filtro, pageable);

        assertNotNull(prenotazioneDTOS);
        List<PrenotazioneDTO> prenotazioneDTOS1 = prenotazioneDTOS.getContent();
        prenotazioneDTOS1.forEach( prenotazione -> assertEquals(utenteUser.getNome(), prenotazione.getNomeUtente()));

    }



}

