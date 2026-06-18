package br.com.oficina.execution.adapters.in.web;

import br.com.oficina.execution.application.ExecutionService;
import br.com.oficina.execution.domain.FilaExecucao;
import br.com.oficina.execution.domain.StatusExecucao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/execucoes")
@Tag(name = "Execuções")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @GetMapping("/{osId}")
    @Operation(summary = "Consulta o estado de execução de uma OS")
    public FilaExecucaoResponse consultar(@PathVariable UUID osId) {
        return FilaExecucaoResponse.from(executionService.buscarPorOsId(osId));
    }

    @PostMapping("/{osId}/diagnostico")
    @Operation(summary = "Inicia o diagnóstico de uma OS")
    public ResponseEntity<FilaExecucaoResponse> iniciarDiagnostico(
        @PathVariable UUID osId,
        @RequestParam @NotBlank String tecnico
    ) {
        return ResponseEntity.ok(FilaExecucaoResponse.from(executionService.iniciarDiagnostico(osId, tecnico)));
    }

    @PostMapping("/{osId}/reparo")
    @Operation(summary = "Inicia o reparo de uma OS")
    public ResponseEntity<FilaExecucaoResponse> iniciarReparo(@PathVariable UUID osId) {
        return ResponseEntity.ok(FilaExecucaoResponse.from(executionService.iniciarReparo(osId)));
    }

    @PostMapping("/{osId}/finalizar")
    @Operation(summary = "Finaliza a execução de uma OS")
    public ResponseEntity<FilaExecucaoResponse> finalizar(@PathVariable UUID osId) {
        return ResponseEntity.ok(FilaExecucaoResponse.from(executionService.finalizar(osId)));
    }

    @PostMapping("/{osId}/falha")
    @Operation(summary = "Registra uma falha na execução de uma OS")
    public ResponseEntity<Void> registrarFalha(@PathVariable UUID osId, @RequestParam String motivo) {
        executionService.registrarFalha(osId, motivo);
        return ResponseEntity.noContent().build();
    }

    public record FilaExecucaoResponse(
        String id,
        String osId,
        StatusExecucao status,
        String tecnicoResponsavel,
        List<FilaExecucao.HistoricoExecucao> historico,
        Instant criadoEm
    ) {
        public static FilaExecucaoResponse from(FilaExecucao f) {
            return new FilaExecucaoResponse(f.id(), f.osId(), f.status(), f.tecnicoResponsavel(), f.historico(), f.criadoEm());
        }
    }
}
