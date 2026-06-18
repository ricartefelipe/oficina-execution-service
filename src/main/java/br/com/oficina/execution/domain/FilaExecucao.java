package br.com.oficina.execution.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Document(collection = "fila_execucao")
public class FilaExecucao {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("os_id")
    private String osId;

    @Field("status")
    private StatusExecucao status;

    @Field("tecnico_responsavel")
    private String tecnicoResponsavel;

    @Field("historico")
    private List<HistoricoExecucao> historico = new ArrayList<>();

    @Field("criado_em")
    private Instant criadoEm;

    @Field("atualizado_em")
    private Instant atualizadoEm;

    protected FilaExecucao() {}

    public static FilaExecucao criar(UUID osId) {
        var f = new FilaExecucao();
        f.id = UUID.randomUUID().toString();
        f.osId = osId.toString();
        f.status = StatusExecucao.NA_FILA;
        f.criadoEm = Instant.now();
        f.historico.add(new HistoricoExecucao(StatusExecucao.NA_FILA, Instant.now(), "OS adicionada a fila"));
        return f;
    }

    public void iniciarDiagnostico(String tecnico) {
        if (status != StatusExecucao.NA_FILA) {
            throw new IllegalStateException("Nao pode iniciar diagnostico no status: " + status);
        }
        this.status = StatusExecucao.EM_DIAGNOSTICO;
        this.tecnicoResponsavel = tecnico;
        this.atualizadoEm = Instant.now();
        historico.add(new HistoricoExecucao(status, Instant.now(), "Diagnostico iniciado por " + tecnico));
    }

    public void iniciarReparo() {
        if (status != StatusExecucao.EM_DIAGNOSTICO) {
            throw new IllegalStateException("Nao pode iniciar reparo no status: " + status);
        }
        this.status = StatusExecucao.EM_REPARO;
        this.atualizadoEm = Instant.now();
        historico.add(new HistoricoExecucao(status, Instant.now(), "Reparo iniciado"));
    }

    public void finalizar() {
        if (status != StatusExecucao.EM_REPARO && status != StatusExecucao.EM_DIAGNOSTICO) {
            throw new IllegalStateException("Nao pode finalizar no status: " + status);
        }
        this.status = StatusExecucao.FINALIZADO;
        this.atualizadoEm = Instant.now();
        historico.add(new HistoricoExecucao(status, Instant.now(), "Execucao finalizada"));
    }

    public void registrarFalha(String motivo) {
        this.status = StatusExecucao.FALHOU;
        this.atualizadoEm = Instant.now();
        historico.add(new HistoricoExecucao(status, Instant.now(), "Falha: " + motivo));
    }

    public void remover() {
        this.status = StatusExecucao.REMOVIDO;
        this.atualizadoEm = Instant.now();
        historico.add(new HistoricoExecucao(status, Instant.now(), "Removido da fila (pagamento cancelado)"));
    }

    public String id() { return id; }
    public String osId() { return osId; }
    public StatusExecucao status() { return status; }
    public String tecnicoResponsavel() { return tecnicoResponsavel; }
    public List<HistoricoExecucao> historico() { return Collections.unmodifiableList(historico); }
    public Instant criadoEm() { return criadoEm; }
    public Instant atualizadoEm() { return atualizadoEm; }

    public record HistoricoExecucao(StatusExecucao status, Instant ocorridoEm, String observacao) {}
}
