package br.com.oficina.execution.adapters.out.messaging;

import br.com.oficina.execution.application.port.out.ExecutionEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
class ExecutionRabbitMqEventPublisher implements ExecutionEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    ExecutionRabbitMqEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String routingKey, Object event) {
        rabbitTemplate.convertAndSend(ExecutionRabbitMqConfig.EXCHANGE, routingKey, event);
    }
}
