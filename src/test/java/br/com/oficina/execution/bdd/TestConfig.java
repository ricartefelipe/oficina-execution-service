package br.com.oficina.execution.bdd;

import br.com.oficina.execution.application.port.out.ExecutionEventPublisherPort;
import br.com.oficina.execution.application.port.out.FilaExecucaoPersistencePort;
import br.com.oficina.execution.domain.FilaExecucao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public ExecutionEventPublisherPort executionEventPublisherPort() {
        return (routingKey, event) -> {};
    }

    @Bean
    @Primary
    public FilaExecucaoPersistencePort filaExecucaoPersistencePort() {
        Map<String, FilaExecucao> store = new HashMap<>();
        return new FilaExecucaoPersistencePort() {
            @Override
            public FilaExecucao salvar(FilaExecucao fila) {
                store.put(fila.osId(), fila);
                return fila;
            }

            @Override
            public Optional<FilaExecucao> buscarPorOsId(UUID osId) {
                return Optional.ofNullable(store.get(osId.toString()));
            }
        };
    }
}
