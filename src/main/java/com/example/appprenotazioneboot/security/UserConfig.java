package com.example.appprenotazioneboot.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("userservice")
@Data
public class UserConfig {
    private String srvUrl;
    private String userId;
    private String password;
}
