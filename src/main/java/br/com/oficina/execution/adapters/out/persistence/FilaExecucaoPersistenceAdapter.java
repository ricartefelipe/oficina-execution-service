package br.com.oficina.execution.adapters.out.persistence;

import br.com.oficina.execution.application.port.out.FilaExecucaoPersistencePort;
import br.com.oficina.execution.domain.FilaExecucao;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class FilaExecucaoPersistenceAdapter implements FilaExecucaoPersistencePort {

    private final FilaExecucaoMongoRepository repository;

    FilaExecucaoPersistenceAdapter(FilaExecucaoMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public FilaExecucao salvar(FilaExecucao fila) {
        return repository.save(fila);
    }

    @Override
    public Optional<FilaExecucao> buscarPorOsId(UUID osId) {
        return repository.findByOsId(osId.toString());
    }
}
