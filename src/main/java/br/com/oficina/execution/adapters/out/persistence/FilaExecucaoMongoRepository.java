package br.com.oficina.execution.adapters.out.persistence;

import br.com.oficina.execution.domain.FilaExecucao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface FilaExecucaoMongoRepository extends MongoRepository<FilaExecucao, String> {
    Optional<FilaExecucao> findByOsId(String osId);
}
