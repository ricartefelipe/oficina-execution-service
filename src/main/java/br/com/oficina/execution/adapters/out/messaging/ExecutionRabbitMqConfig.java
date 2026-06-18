package br.com.oficina.execution.adapters.out.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutionRabbitMqConfig {

    public static final String EXCHANGE = "oficina.events";
    public static final String DLX = "oficina.dlx";

    @Bean
    public TopicExchange executionMainExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange executionDlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter executionMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private Queue durableWithDlq(String name) {
        return QueueBuilder.durable(name)
            .withArgument("x-dead-letter-exchange", DLX)
            .withArgument("x-dead-letter-routing-key", name + ".dlq")
            .build();
    }

    @Bean public Queue queueExecPagamentoConfirmado() { return durableWithDlq("execution-service.pagamento.confirmado"); }
    @Bean public Queue queueExecPagamentoFalhou() { return durableWithDlq("execution-service.pagamento.falhou"); }

    @Bean
    public Binding bindExecPagamentoConfirmado(Queue queueExecPagamentoConfirmado, TopicExchange executionMainExchange) {
        return BindingBuilder.bind(queueExecPagamentoConfirmado).to(executionMainExchange).with("pagamento.confirmado");
    }

    @Bean
    public Binding bindExecPagamentoFalhou(Queue queueExecPagamentoFalhou, TopicExchange executionMainExchange) {
        return BindingBuilder.bind(queueExecPagamentoFalhou).to(executionMainExchange).with("pagamento.falhou");
    }
}
