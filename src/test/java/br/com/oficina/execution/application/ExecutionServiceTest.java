package br.com.oficina.execution.application;

import br.com.oficina.execution.application.port.out.ExecutionEventPublisherPort;
import br.com.oficina.execution.application.port.out.FilaExecucaoPersistencePort;
import br.com.oficina.execution.domain.FilaExecucao;
import br.com.oficina.execution.domain.StatusExecucao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock FilaExecucaoPersistencePort persistence;
    @Mock ExecutionEventPublisherPort events;
    @InjectMocks ExecutionService service;

    @Test
    void deveAdicionarNaFila() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        when(persistence.salvar(any())).thenReturn(fila);

        var resultado = service.adicionarNaFila(osId);

        assertThat(resultado.status()).isEqualTo(StatusExecucao.NA_FILA);
        verify(persistence).salvar(any());
    }

    @Test
    void deveFinalizar() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        fila.iniciarDiagnostico("Tecnico");
        fila.iniciarReparo();
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(fila));
        when(persistence.salvar(any())).thenReturn(fila);

        service.finalizar(osId);

        assertThat(fila.status()).isEqualTo(StatusExecucao.FINALIZADO);
        verify(events).publish(eq("execucao.finalizada"), any());
    }

    @Test
    void devePublicarExecucaoFalhouAoRegistrarFalha() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(fila));
        when(persistence.salvar(any())).thenReturn(fila);

        service.registrarFalha(osId, "Peca em falta");

        verify(events).publish(eq("execucao.falhou"), any());
    }

    @Test
    void devePublicarExecucaoIniciadaAoIniciarDiagnostico() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(fila));
        when(persistence.salvar(any())).thenReturn(fila);

        service.iniciarDiagnostico(osId, "Mecanico Felipe");

        verify(events).publish(eq("execucao.iniciada"), any());
    }

    @Test
    void deveRegistrarFalhaComMotivoNulo() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(fila));
        when(persistence.salvar(any())).thenReturn(fila);

        service.registrarFalha(osId, null);

        verify(events).publish(eq("execucao.falhou"), any());
    }

    @Test
    void deveRemoverDaFilaSemErroSeNaoExistir() {
        var osId = UUID.randomUUID();
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> service.removerDaFila(osId));
        verify(persistence, never()).salvar(any());
    }
}
