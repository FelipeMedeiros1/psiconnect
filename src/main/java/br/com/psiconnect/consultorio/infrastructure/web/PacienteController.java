package br.com.psiconnect.consultorio.infrastructure.web;

import br.com.psiconnect.consultorio.application.paciente.PacienteService;
import br.com.psiconnect.consultorio.application.paciente.dto.*;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<DadosDetalhePaciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uriBuilder) {
        try {
            var pacienteCadastrado = pacienteService.cadastrar(dados);
            var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteCadastrado.id()).toUri();
            return ResponseEntity.created(uri).body(pacienteCadastrado);
        } catch (ConsultorioException e) {
            log.warn("event=request_rejected reason=patient_registration_rule status=400");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    ResponseEntity<Page<DadosListagemPaciente>> listar(
            @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable paginacao){
        return ResponseEntity.ok(pacienteService.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhePaciente> buscarPorId(@PathVariable Long id) {
        try {
            DadosDetalhePaciente paciente = pacienteService.buscarPorId(id);
            return ResponseEntity.ok(paciente);
        } catch (ConsultorioException e) {
            log.warn("event=request_rejected reason=patient_not_found status=404");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhePaciente>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(pacienteService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhePaciente> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPaciente dados) {
        if (!id.equals(dados.id())) {
            log.warn("event=request_rejected reason=patient_id_mismatch status=400");
            return ResponseEntity.badRequest().build();
        }
        var pacienteAtualizado = pacienteService.atualizar(dados);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @PutMapping("/{id}/local-atendimento")
    public ResponseEntity<DadosDetalhePaciente> associarLocal(
            @PathVariable Long id, @RequestBody DadosLocalPaciente dados) {
        return ResponseEntity.ok(pacienteService.associarLocal(id, dados.localAtendimentoId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/alta")
    public ResponseEntity<Void> altaPaciente(@PathVariable Long id, @RequestBody @Valid DadosAltaPaciente dados) {
        pacienteService.altaPaciente(id, dados);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativarPaciente(@PathVariable Long id) {
        pacienteService.reativarPaciente(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/alta")
    public ResponseEntity<Void> altaPacienteLegado(@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        pacienteService.altaPaciente(dados.id(), new DadosAltaPaciente(dados.motivoAlta(), "Sistema"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/altas")
    public ResponseEntity<List<DadosHistoricoAlta>> listarAltas() {
        return ResponseEntity.ok(pacienteService.listarAltas());
    }

    @GetMapping("/relatorio/{inicioMes}/{fimMes}")
    public ResponseEntity<List<DadosRelatorioPacienteMensal>> gerarRelatorioMensal(
            @PathVariable String inicioMes,
            @PathVariable String fimMes) {
        LocalDateTime inicio = LocalDateTime.parse(inicioMes,  DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime fim = LocalDateTime.parse(fimMes, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ResponseEntity.ok(pacienteService.gerarRelatorioMensal(inicio, fim));
    }
}
