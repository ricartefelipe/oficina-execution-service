package br.com.oficina.execution.adapters.out.persistence;

import br.com.oficina.execution.domain.FilaExecucao;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FilaExecucaoPersistenceAdapterTest {

    private final FilaExecucaoMongoRepository repository = Mockito.mock(FilaExecucaoMongoRepository.class);
    private final FilaExecucaoPersistenceAdapter adapter = new FilaExecucaoPersistenceAdapter(repository);

    @Test
    void salvar_deveDelegar() {
        var fila = FilaExecucao.criar(UUID.randomUUID());
        when(repository.save(fila)).thenReturn(fila);

        var resultado = adapter.salvar(fila);

        assertThat(resultado).isEqualTo(fila);
        verify(repository).save(fila);
    }

    @Test
    void buscarPorOsId_deveDelegar() {
        var osId = UUID.randomUUID();
        var fila = FilaExecucao.criar(osId);
        when(repository.findByOsId(osId.toString())).thenReturn(Optional.of(fila));

        var resultado = adapter.buscarPorOsId(osId);

        assertThat(resultado).isPresent();
        verify(repository).findByOsId(osId.toString());
    }
}
