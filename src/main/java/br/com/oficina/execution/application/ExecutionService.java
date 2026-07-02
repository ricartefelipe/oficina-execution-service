package br.com.oficina.execution.application;

import br.com.oficina.execution.application.port.out.ExecutionEventPublisherPort;
import br.com.oficina.execution.application.port.out.FilaExecucaoPersistencePort;
import br.com.oficina.execution.domain.FilaExecucao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);

    private final FilaExecucaoPersistencePort persistence;
    private final ExecutionEventPublisherPort events;

    public ExecutionService(FilaExecucaoPersistencePort persistence, ExecutionEventPublisherPort events) {
        this.persistence = persistence;
        this.events = events;
    }

    public FilaExecucao adicionarNaFila(UUID osId) {
        var fila = FilaExecucao.criar(osId);
        var salva = persistence.salvar(fila);
        log.info("OS {} adicionada a fila de execucao", osId);
        return salva;
    }

    public void removerDaFila(UUID osId) {
        persistence.buscarPorOsId(osId).ifPresent(fila -> {
            fila.remover();
            persistence.salvar(fila);
            log.info("OS {} removida da fila (pagamento cancelado)", osId);
        });
    }

    public FilaExecucao iniciarDiagnostico(UUID osId, String tecnico) {
        var fila = buscarOuFalhar(osId);
        fila.iniciarDiagnostico(tecnico);
        var salva = persistence.salvar(fila);
        events.publish("execucao.iniciada", buildEvent(osId, "tecnico", tecnico));
        return salva;
    }

    public FilaExecucao iniciarReparo(UUID osId) {
        var fila = buscarOuFalhar(osId);
        fila.iniciarReparo();
        return persistence.salvar(fila);
    }

    public FilaExecucao finalizar(UUID osId) {
        var fila = buscarOuFalhar(osId);
        fila.finalizar();
        var salva = persistence.salvar(fila);
        log.info("OS {} finalizada com sucesso", osId);
        events.publish("execucao.finalizada", buildEvent(osId));
        return salva;
    }

    public FilaExecucao registrarFalha(UUID osId, String motivo) {
        var fila = buscarOuFalhar(osId);
        fila.registrarFalha(motivo);
        var salva = persistence.salvar(fila);
        log.error("OS {} com falha na execucao: {}", osId, motivo != null ? motivo.replaceAll("[^a-zA-Z0-9 ._-]", "_") : "null");
        events.publish("execucao.falhou", buildEvent(osId, "motivo", motivo));
        return salva;
    }

    public FilaExecucao buscarPorOsId(UUID osId) {
        return buscarOuFalhar(osId);
    }

    private FilaExecucao buscarOuFalhar(UUID osId) {
        return persistence.buscarPorOsId(osId)
            .orElseThrow(() -> new NoSuchElementException("Execucao nao encontrada para OS: " + osId));
    }

    private Map<String, Object> buildEvent(UUID osId, String... pairs) {
        Map<String, Object> event = new HashMap<>();
        event.put("osId", osId.toString());
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            event.put(pairs[i], pairs[i + 1]);
        }
        return event;
    }
}
