package br.com.oficina.execution.adapters.out.messaging;

import br.com.oficina.execution.application.ExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class ExecutionSagaEventListener {

    private static final Logger log = LoggerFactory.getLogger(ExecutionSagaEventListener.class);

    private final ExecutionService executionService;

    public ExecutionSagaEventListener(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @RabbitListener(queues = "execution-service.pagamento.confirmado")
    public void onPagamentoConfirmado(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.info("Saga: pagamento confirmado, adicionando OS {} a fila de execucao", osId);
        executionService.adicionarNaFila(osId);
    }

    @RabbitListener(queues = "execution-service.pagamento.falhou")
    public void onPagamentoFalhou(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.warn("Saga: pagamento falhou, removendo OS {} da fila", osId);
        executionService.removerDaFila(osId);
    }
}
