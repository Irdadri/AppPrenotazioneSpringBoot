package com.example.appprenotazioneboot.service;

import com.example.appprenotazioneboot.security.UserConfig;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.UtenteApi;
import org.openapitools.client.auth.HttpBasicAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiService {

    @Value("${userservice.srvUrl:http://localhost:9090}")
    private String userServiceUrl;

    private final UserConfig config;

    public ApiService(UserConfig config) {
        this.config = config;
    }

    @Bean
    public UtenteApi utenteApi() {

        ApiClient apiClient = new ApiClient();

        apiClient.setBasePath(userServiceUrl + "/auth/");

        HttpBasicAuth auth =
                (HttpBasicAuth) apiClient.getAuthentication("basicAuth");

        auth.setUsername(config.getUserId());
        auth.setPassword(config.getPassword());

        return new UtenteApi(apiClient);
    }
}