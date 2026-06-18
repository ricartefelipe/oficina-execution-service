package br.com.oficina.execution.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class FilaExecucaoTest {

    @Test
    void deveCriarComStatusNaFila() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        assertThat(fila.status()).isEqualTo(StatusExecucao.NA_FILA);
        assertThat(fila.historico()).hasSize(1);
        assertThat(fila.criadoEm()).isNotNull();
    }

    @Test
    void deveIniciarDiagnostico() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.iniciarDiagnostico("Tecnico Joao");
        assertThat(fila.status()).isEqualTo(StatusExecucao.EM_DIAGNOSTICO);
        assertThat(fila.tecnicoResponsavel()).isEqualTo("Tecnico Joao");
        assertThat(fila.historico()).hasSize(2);
    }

    @Test
    void deveIniciarReparo() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.iniciarDiagnostico("Tecnico");
        fila.iniciarReparo();
        assertThat(fila.status()).isEqualTo(StatusExecucao.EM_REPARO);
    }

    @Test
    void deveFinalizar() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.iniciarDiagnostico("Tecnico");
        fila.iniciarReparo();
        fila.finalizar();
        assertThat(fila.status()).isEqualTo(StatusExecucao.FINALIZADO);
    }

    @Test
    void deveFinalizarDiretamenteDoDiagnostico() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.iniciarDiagnostico("Tecnico");
        fila.finalizar();
        assertThat(fila.status()).isEqualTo(StatusExecucao.FINALIZADO);
    }

    @Test
    void deveFalharAoIniciarDiagnosticoNaoNaFila() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.iniciarDiagnostico("Tecnico");
        assertThatThrownBy(() -> fila.iniciarDiagnostico("Outro"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveRegistrarFalha() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.registrarFalha("Peca indisponivel");
        assertThat(fila.status()).isEqualTo(StatusExecucao.FALHOU);
    }

    @Test
    void deveRemover() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        fila.remover();
        assertThat(fila.status()).isEqualTo(StatusExecucao.REMOVIDO);
    }

    @Test
    void osIdDeveCorresponder() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        assertThat(fila.osId()).isEqualTo(osId.toString());
    }
}
