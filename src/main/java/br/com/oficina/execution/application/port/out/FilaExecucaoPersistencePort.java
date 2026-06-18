package br.com.oficina.execution.application.port.out;

import br.com.oficina.execution.domain.FilaExecucao;

import java.util.Optional;
import java.util.UUID;

public interface FilaExecucaoPersistencePort {
    FilaExecucao salvar(FilaExecucao fila);
    Optional<FilaExecucao> buscarPorOsId(UUID osId);
}
