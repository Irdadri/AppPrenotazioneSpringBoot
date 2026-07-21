package com.example.appprenotazioneboot.config;

import com.example.appprenotazioneboot.dto.PrenotazioneDTO;
import com.example.appprenotazioneboot.dto.UtenteDTO;
import com.example.appprenotazioneboot.dto.UtenteHttp;
import com.example.appprenotazioneboot.dto.UtenteRequest;
import com.example.appprenotazioneboot.entities.Prenotazione;
import com.example.appprenotazioneboot.entities.TipoUtenteEnum;
import com.example.appprenotazioneboot.entities.Utente;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);


        modelMapper.addMappings(prenotazioneDTOPropertyMap);
        modelMapper.addMappings(utentePropertyMap);
        modelMapper.addMappings(utenteDTOPropertyMap);
        modelMapper.addConverter(enumConverter);

        return modelMapper;
    }

    PropertyMap<Prenotazione, PrenotazioneDTO> prenotazioneDTOPropertyMap = new PropertyMap<Prenotazione, PrenotazioneDTO>() {
        @Override
        protected void configure() {
            map().setNomeUtente(source.getUtente().getNome());
            map().setCognomeUtente(source.getUtente().getCognome());
            map().setCitta(source.getPostazione().getStanza().getSede().getCitta());
            map().setIndirizzo(source.getPostazione().getStanza().getSede().getIndirizzo());
            map().setNStanza(source.getPostazione().getStanza().getNStanza());
            map().setNPostazione(source.getPostazione().getId());
        }
    };

    PropertyMap<UtenteRequest, Utente> utentePropertyMap = new PropertyMap<UtenteRequest, Utente>() {
        @Override
        protected void configure() {
            map().getSede().setId(source.getIdSede());
        }
    };
    /*
    PropertyMap<UtenteHttp, Utente> utenteHttpUtentePropertyMap = new PropertyMap<UtenteHttp, Utente>() {
        @Override
        protected void configure() {
            map().getSede().setId(source.getIdSede());
        }
    }

     */

    PropertyMap<Utente, UtenteDTO> utenteDTOPropertyMap = new PropertyMap<Utente, UtenteDTO>() {
        @Override
        protected void configure() {
            map().setPaese(source.getSede().getPaese());
            map().setCitta(source.getSede().getCitta());
            map().setRegione(source.getSede().getRegione());
            map().setIndirizzo(source.getSede().getIndirizzo());
        }
    };

    Converter<String, TipoUtenteEnum> enumConverter = new AbstractConverter<String, TipoUtenteEnum>() {

        @Override
        protected TipoUtenteEnum convert(String source) {
            return source == null ? null :TipoUtenteEnum.valueOf(source);
        }
    };
}
