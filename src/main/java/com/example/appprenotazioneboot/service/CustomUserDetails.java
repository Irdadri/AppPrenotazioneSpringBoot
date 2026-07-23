package com.example.appprenotazioneboot.service;


import com.example.appprenotazioneboot.security.UserConfig;


import lombok.extern.java.Log;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.model.UtenteHttp;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Log
public class CustomUserDetails implements UserDetailsService {


    private final UtenteApi utenteApi;

    public CustomUserDetails(UtenteApi utenteApi) {
        this.utenteApi = utenteApi;
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

        UtenteHttp utente = utenteApi.getHttpUser(email);

        if (utente == null) {

            log.warning("Utente %s non Trovato!!" + email);
            throw new UsernameNotFoundException("Utente %s non Trovato!!");

        }

        User.UserBuilder builder = null;
        builder = org.springframework.security.core.userdetails.User.withUsername(utente.getEmail());
        builder.password(utente.getPassword());
        String[] profili = {"ROLE_" + utente.getTipoUtente()};

        builder.authorities(profili);

        return builder.build();


    }

}
