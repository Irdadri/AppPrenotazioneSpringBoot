package com.example.appprenotazioneboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//modello per form utente
public class UtenteRequest {
    @NotBlank
    @Size(min = 1, max = 45)
    private String nome;

    @NotBlank
    @Size(min = 1, max = 45)
    private String cognome;

    @NotBlank
    @Size(min = 5, max = 45)
    private String email;

    @NotBlank
    @Size(min = 1, max = 64)
    private String password;

    @NotBlank
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private String telefono;

    @NotBlank
    private String tipoUtente;
    
    @NotNull
    private Integer idSede;
}
