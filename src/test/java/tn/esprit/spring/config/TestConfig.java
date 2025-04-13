package tn.esprit.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import tn.esprit.spring.mappers.InstructorMapper;
import tn.esprit.spring.mappers.PisteMapper;
import tn.esprit.spring.mappers.RegistrationMapper;
import tn.esprit.spring.mappers.SubscriptionMapper;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public InstructorMapper instructorMapper() {
        return new InstructorMapper();
    }
    
    @Bean
    public PisteMapper pisteMapper() {
        return new PisteMapper();
    }
    
    @Bean
    public RegistrationMapper registrationMapper() {
        return new RegistrationMapper();
    }
    
    @Bean
    public SubscriptionMapper subscriptionMapper() {
        return new SubscriptionMapper();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
