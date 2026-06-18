package br.com.oficina.execution.adapters.out.messaging;

import br.com.oficina.execution.application.ExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;

class ExecutionSagaEventListenerTest {

    private ExecutionService executionService;
    private ExecutionSagaEventListener listener;

    @BeforeEach
    void setUp() {
        executionService = Mockito.mock(ExecutionService.class);
        listener = new ExecutionSagaEventListener(executionService);
    }

    @Test
    void onPagamentoConfirmado_deveAdicionarNaFila() {
        UUID osId = UUID.randomUUID();
        listener.onPagamentoConfirmado(Map.of("osId", osId.toString()));
        verify(executionService).adicionarNaFila(osId);
    }

    @Test
    void onPagamentoFalhou_deveRemoverDaFila() {
        UUID osId = UUID.randomUUID();
        listener.onPagamentoFalhou(Map.of("osId", osId.toString()));
        verify(executionService).removerDaFila(osId);
    }
}
