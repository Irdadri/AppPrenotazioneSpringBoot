package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.security.UserConfig;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.auth.HttpBasicAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiService {
    private final UserConfig Config;

    public ApiService(UserConfig config) {
        Config = config;
    }

    @Bean
    public UtenteApi utenteApi(){
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:9090/auth/cerca/");

        HttpBasicAuth auth =
                (HttpBasicAuth) apiClient.getAuthentication("basicAuth");

        auth.setUsername(Config.getUserId());
        auth.setPassword(Config.getPassword());

        return new UtenteApi(apiClient);
    }
}
