package com.example.FabriqBackend.config;

import com.example.FabriqBackend.service.aws.S3Service;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public S3Service s3Service() {
        return mock(S3Service.class);
    }
}

