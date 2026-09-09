package com.example.appprenotazioneboot.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    NewTopic notification(){
        return TopicBuilder.name("notification")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
