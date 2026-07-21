package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.dto.UtenteDTO;
import com.example.appprenotazioneboot.dto.UtenteFiltro;
import com.example.appprenotazioneboot.dto.UtenteRequest;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.Sede;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import com.example.appprenotazioneboot.repository.SedeRepository;
import com.example.appprenotazioneboot.repository.UtenteRepository;
import jakarta.persistence.criteria.Join;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
public class UtenteServiceImpl implements  UtenteService {
    private final ModelMapper modelMapper;
    private final SedeRepository sedeRepository;
    private UtenteRepository repository;
    private final PasswordEncoder encoder;

    public UtenteServiceImpl(UtenteRepository repository, ModelMapper modelMapper, SedeRepository sedeRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.sedeRepository = sedeRepository;
        this.encoder = encoder;
    }

    public Utente loginUtente(String email, String password) {
        Utente utente = repository.findByEmail(email);

        if (utente != null) {
            String encryptedPassword = null;
            try {
                encryptedPassword = passwordEncrypting(password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }


            if (encryptedPassword.equals(utente.getPassword())) {
                return utente;
            }
        }
        return null;
    }

    public void inserisciUtente(UtenteRequest utenteRequest) throws Exception {
        /*
        try {
            utenteRequest.setPassword(passwordEncrypting(utenteRequest.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }*/
        Utente utente = modelMapper.map(utenteRequest, Utente.class);
        Utente checkUtente = repository.findUtenteById(utente.getId());
        if(utente != null) {
            if (utenteRequest.getIdSede() != null) {
                Sede sede = sedeRepository.findSedeById(utenteRequest.getIdSede());
                if (sede != null) {
                    utente.setSede(sede);
                } else {
                    utente.setSede(new Sede());
                }
            } else {
                utente.setSede(new Sede());
            }
            repository.save(utente);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("http://localhost:9090/auth/creaUtente/" + utente.getId(), utenteRequest, UtenteRequest.class);
        } else {
            throw new Exception("user already exists");
        }
    }


    public String passwordEncrypting(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(encodedhash);
    }

    public Page<UtenteDTO> getAllUtenti(Pageable pageable){
        Page<Utente> utenti = repository.findAll(pageable);
        return utenti.map(utente -> modelMapper.map(utente, UtenteDTO.class));
    }

    public static Specification<Utente> hasEmail(String email){
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("email"), email);
        });
    }

    public static Specification<Utente> hasCitta(String citta){
        return ((root, query, criteriaBuilder) -> {
            Join<Utente, Sede> utenteSede = root.join("sede");
            return criteriaBuilder.equal(utenteSede.get("citta"), citta);
        });
    }

    public static Specification<Utente> hasIndirizzo(String indirizzo){
        return ((root, query, criteriaBuilder) -> {
            Join<Utente, Sede> utenteSede = root.join("sede");
            return criteriaBuilder.equal(utenteSede.get("indirizzo"), indirizzo);
        });
    }

    public Page<UtenteDTO> getUtentiByFilter(UtenteFiltro utenteFiltro, Pageable pageable){

        Specification<Utente> specification = Specification.where(null);

        if(utenteFiltro.getEmail() != null){
            specification = specification.and(hasEmail(utenteFiltro.getEmail()));
        }

        if(utenteFiltro.getCitta() != null){
            specification = specification.and(hasCitta(utenteFiltro.getCitta()));
        }

        if(utenteFiltro.getIndirizzo() != null){
            specification = specification.and(hasIndirizzo(utenteFiltro.getIndirizzo()));
        }


        return repository.findAll(specification, pageable)
                .map(utente -> modelMapper.map(utente, UtenteDTO.class));

    }

    @Override
    public UtenteDTO getUtente(int id) {
        return modelMapper.map(repository.findUtenteById(id), UtenteDTO.class);
    }

    @Override
    public UtenteDTO aggiornaUtente(UtenteRequest utenteRequest, int id) {
        Utente utente = repository.findUtenteById(id);
        if(utente != null){
            if(utenteRequest.getNome() != null){
                utente.setNome(utenteRequest.getNome());
            }
            if(utenteRequest.getCognome() != null){
                utente.setCognome(utenteRequest.getCognome());
            }
            if(utenteRequest.getPassword() != null){
                utente.setPassword(utente.getPassword());
            }
            if(utenteRequest.getEmail() != null){
                utente.setEmail(utente.getEmail());
            }
            if(utenteRequest.getTelefono() != null){
                utente.setTelefono(utente.getTelefono());
            }
            if(utenteRequest.getTipoUtente() != null){
                utente.setTipoUtente(TipoUtenteEnum.valueOf(utenteRequest.getTipoUtente()));
            }
            if(utenteRequest.getIdSede() != null){
                utente.setSede(sedeRepository.findSedeById(utenteRequest.getIdSede()));
            }

            repository.save(utente);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("http://localhost:9090/auth/creaUtente/" + utente.getId(), utenteRequest, UtenteRequest.class);
            return modelMapper.map(utente, UtenteDTO.class);
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        Utente utente = repository.findUtenteById(id);
        if(utente != null){
            repository.delete(utente);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete("http://localhost:9090/auth/delete/" + utente.getId());
        }
    }

    @Override
    public Utente getUtenteByEmail(String email) {
        return repository.findByEmail(email);
    }


}
