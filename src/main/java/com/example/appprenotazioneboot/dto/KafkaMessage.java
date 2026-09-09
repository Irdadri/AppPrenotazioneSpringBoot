package com.example.appprenotazioneboot.dto;

import lombok.*;
import org.openapitools.model.PrenotazioneDTO;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessage {

    /*
    private String tipoNotifica;
    private PrenotazioneDTO prenotazioneDTO;

     */

    private String tipoNotifica;
    private Map<String, String> properties = new HashMap<>();
}
