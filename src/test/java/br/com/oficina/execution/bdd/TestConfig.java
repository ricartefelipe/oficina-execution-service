package br.com.oficina.execution.bdd;

import br.com.oficina.execution.application.port.out.ExecutionEventPublisherPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public ExecutionEventPublisherPort executionEventPublisherPort() {
        return (routingKey, event) -> {};
    }
}
