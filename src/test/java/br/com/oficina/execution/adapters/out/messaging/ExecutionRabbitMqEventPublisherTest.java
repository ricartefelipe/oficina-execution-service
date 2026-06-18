package br.com.oficina.execution.adapters.out.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static org.mockito.Mockito.verify;

class ExecutionRabbitMqEventPublisherTest {

    @Test
    void publish_deveEncaminharParaRabbitTemplate() {
        RabbitTemplate template = Mockito.mock(RabbitTemplate.class);
        ExecutionRabbitMqEventPublisher publisher = new ExecutionRabbitMqEventPublisher(template);
        Object event = Map.of("osId", "abc");

        publisher.publish("execucao.finalizada", event);

        verify(template).convertAndSend(ExecutionRabbitMqConfig.EXCHANGE, "execucao.finalizada", event);
    }
}
