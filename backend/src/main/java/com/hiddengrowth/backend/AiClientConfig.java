package com.hiddengrowth.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiClientConfig {

    @Bean
    RestClient aiRestClient(@Value("${ai.base-url}") String baseUrl){
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
