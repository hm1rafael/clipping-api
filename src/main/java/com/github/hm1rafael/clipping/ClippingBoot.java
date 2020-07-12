package com.github.hm1rafael.clipping;

import com.github.hm1rafael.clipping.converters.ConvertersConfiguration;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.NoCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Clock;

@SpringBootApplication
@EnableDatastoreRepositories
@Import(ConvertersConfiguration.class)
public class ClippingBoot {

    public static void main(String[] args) {
        SpringApplication.run(ClippingBoot.class);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.cloud.gcp.datastore.emulator.enabled", havingValue = "true")
    public CredentialsProvider googleCredentials() {
        return NoCredentials::getInstance;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
