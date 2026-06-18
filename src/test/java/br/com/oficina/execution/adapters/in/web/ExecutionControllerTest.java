package br.com.oficina.execution.adapters.in.web;

import br.com.oficina.execution.application.ExecutionService;
import br.com.oficina.execution.domain.FilaExecucao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExecutionControllerTest {

    private MockMvc mockMvc;
    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionService = Mockito.mock(ExecutionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ExecutionController(executionService)).build();
    }

    private FilaExecucao filaFixture() {
        return FilaExecucao.criar(UUID.randomUUID());
    }

    @Test
    void consultar_deveRetornarOk() throws Exception {
        when(executionService.buscarPorOsId(any())).thenReturn(filaFixture());

        mockMvc.perform(get("/admin/execucoes/{osId}", UUID.randomUUID()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("NA_FILA"));
    }

    @Test
    void iniciarDiagnostico_deveRetornarOk() throws Exception {
        when(executionService.iniciarDiagnostico(any(), anyString())).thenReturn(filaFixture());

        mockMvc.perform(post("/admin/execucoes/{osId}/diagnostico", UUID.randomUUID())
                .param("tecnico", "João"))
            .andExpect(status().isOk());
    }

    @Test
    void iniciarReparo_deveRetornarOk() throws Exception {
        when(executionService.iniciarReparo(any())).thenReturn(filaFixture());

        mockMvc.perform(post("/admin/execucoes/{osId}/reparo", UUID.randomUUID()))
            .andExpect(status().isOk());
    }

    @Test
    void finalizar_deveRetornarOk() throws Exception {
        when(executionService.finalizar(any())).thenReturn(filaFixture());

        mockMvc.perform(post("/admin/execucoes/{osId}/finalizar", UUID.randomUUID()))
            .andExpect(status().isOk());
    }

    @Test
    void registrarFalha_deveRetornarNoContent() throws Exception {
        when(executionService.registrarFalha(any(), anyString())).thenReturn(filaFixture());

        mockMvc.perform(post("/admin/execucoes/{osId}/falha", UUID.randomUUID())
                .param("motivo", "Peça indisponível"))
            .andExpect(status().isNoContent());
    }
}
