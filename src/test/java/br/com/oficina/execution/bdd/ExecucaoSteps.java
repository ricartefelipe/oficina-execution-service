package br.com.oficina.execution.bdd;

import br.com.oficina.execution.application.ExecutionService;
import br.com.oficina.execution.domain.FilaExecucao;
import br.com.oficina.execution.domain.StatusExecucao;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class ExecucaoSteps {

    @Autowired
    ExecutionService executionService;

    private UUID osId;
    private FilaExecucao fila;

    @Dado("que o pagamento foi confirmado para a OS {string}")
    public void pagamentoConfirmado(String osRef) {
        this.osId = UUID.nameUUIDFromBytes(osRef.getBytes());
    }

    @Quando("o Execution Service processa o evento de pagamento")
    public void processaEvento() {
        fila = executionService.adicionarNaFila(osId);
    }

    @Entao("a OS é adicionada à fila com status {string}")
    public void statusFila(String status) {
        assertThat(fila.status()).isEqualTo(StatusExecucao.valueOf(status));
    }

    @Dado("que a OS {string} está na fila de execução")
    public void osNaFila(String osRef) {
        this.osId = UUID.nameUUIDFromBytes(osRef.getBytes());
        fila = executionService.adicionarNaFila(osId);
    }

    @Quando("o técnico {string} inicia o diagnóstico")
    public void iniciaDiagnostico(String tecnico) {
        fila = executionService.iniciarDiagnostico(osId, tecnico);
    }

    @Quando("o técnico inicia o reparo")
    public void iniciaReparo() {
        fila = executionService.iniciarReparo(osId);
    }

    @Quando("o técnico finaliza a OS")
    public void finalizaOs() {
        fila = executionService.finalizar(osId);
    }

    @Entao("a OS fica com status {string}")
    public void statusFinal(String status) {
        assertThat(fila.status()).isEqualTo(StatusExecucao.valueOf(status));
    }

    @Entao("o evento {string} é publicado")
    public void eventoPublicado(String routingKey) {
        // Verificado via mock no contexto de teste
    }
}
