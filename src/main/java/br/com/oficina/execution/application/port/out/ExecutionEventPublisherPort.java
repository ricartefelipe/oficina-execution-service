package br.com.oficina.execution.application.port.out;

public interface ExecutionEventPublisherPort {
    void publish(String routingKey, Object event);
}
