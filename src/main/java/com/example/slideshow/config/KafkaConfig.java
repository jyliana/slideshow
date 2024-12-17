package com.example.slideshow.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

  @Bean
  NewTopic topicAddDeleteImage() {
    return TopicBuilder
            .name("t-add-delete-image")
            .partitions(2)
            .build();
  }

}