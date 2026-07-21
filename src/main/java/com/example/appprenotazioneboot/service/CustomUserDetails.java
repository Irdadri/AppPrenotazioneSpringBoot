package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.dto.UtenteHttp;
import com.example.appprenotazioneboot.security.UserConfig;


import lombok.extern.java.Log;

import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


@Service
@Log
public class CustomUserDetails implements UserDetailsService {

    private UserConfig Config;

    public CustomUserDetails(UserConfig config) {
        Config = config;
    }

    /* dato che il login è effettuato altrove
    potrei rimuovere del tutto questo metodo
    e costruire l'oggetto UserDetails nel filtro jwt
    estraendo i dati che mi servono dai Claims
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        if (email == null || email.length() < 2) {
            throw new UsernameNotFoundException("Nome utente assente o non valido");
        }

        UtenteHttp utente = this.GetHttpValue(email);

        if (utente == null) {

            log.warning("Utente %s non Trovato!!" + email);
            throw new UsernameNotFoundException("Utente %s non Trovato!!");

        }

        User.UserBuilder builder = null;
        builder = org.springframework.security.core.userdetails.User.withUsername(utente.getEmail());
        builder.password(utente.getPassword());
        String[] profili = {"ROLE_" + utente.getTipoUtente().name()};

        builder.authorities(profili);

        return builder.build();


    }

    private UtenteHttp GetHttpValue(String email) {

        URI url = null;

        try {
            String SrvUrl = Config.getSrvUrl();

            url = new URI(SrvUrl + email);
        } catch (URISyntaxException e) {

            e.printStackTrace();
        }

        RestTemplate restTemplate = new RestTemplate();
        log.warning(Config.getUserId() + "----" + Config.getPassword());
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(Config.getUserId(), Config.getPassword()));

        UtenteHttp utente = null;

        try {
            utente = restTemplate.getForObject(url, UtenteHttp.class);
        } catch (Exception e) {

            log.warning("Connessione al servizio di autenticazione non riuscita!!");

        }

        return utente;
    }



}
