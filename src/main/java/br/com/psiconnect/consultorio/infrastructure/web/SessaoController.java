package br.com.psiconnect.consultorio.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import br.com.psiconnect.consultorio.application.consulta.SessaoService;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosDetalhamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosListagemSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosRelatorioConsultaMensal;

import br.com.psiconnect.consultorio.application.consulta.dto.DadosAtualizacaoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosHistoricoAtendimento;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosRegistroAtendimento;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Sessões e atendimentos", description = "Agendamento, confirmação, evolução, histórico e relatórios financeiros dos atendimentos.")
@RestController
@RequestMapping("/sessoes")
public class SessaoController {
    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @Operation(summary = "Agendar sessão", description = "Agenda uma consulta para paciente e psicólogo. Se o psicólogo não for informado, o sistema seleciona um disponível pela especialidade.")
    @PostMapping
    public ResponseEntity<DadosDetalhamentoSessao> agendar(
            @RequestBody @Valid DadosAgendamentoSessao dados, UriComponentsBuilder uriBuilder) {
        var sessao = service.agendar(dados);
        var uri = uriBuilder.path("/sessoes/{id}").buildAndExpand(sessao.id()).toUri();
        return ResponseEntity.created(uri).body(sessao);
    }

    @Operation(summary = "Listar sessões", description = "Retorna sessões paginadas, separando agendamentos pendentes e atendimentos realizados. Aceita page, size e sort.")
    @GetMapping
    public ResponseEntity<Page<DadosListagemSessao>> listar(
            @PageableDefault(size = 20, sort = {"compareceu", "data"}) Pageable paginacao) {
        return ResponseEntity.ok(service.listarSessoes(paginacao));
    }
    @Operation(summary = "Consultar sessão por ID", description = "Retorna data, paciente, psicólogo, valor, presença e evolução da sessão informada.")
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoSessao> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Reagendar ou atualizar sessão", description = "Altera data, paciente, psicólogo ou valor de uma sessão ainda editável, validando conflitos de agenda.")
    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoSessao> atualizar(
            @PathVariable Long id, @RequestBody @Valid DadosAtualizacaoSessao dados) {
        if (!id.equals(dados.id())) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.atualizar(dados));
    }

    @Operation(summary = "Cancelar sessão", description = "Remove a sessão identificada pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Confirmar atendimento e registrar evolução", description = "Marca presença na sessão e registra a evolução clínica informada, limitada a 500 caracteres.")
    @PutMapping("/{id}/atendimento")
    public ResponseEntity<Void> registrarAtendimento(
            @PathVariable Long id, @RequestBody @Valid DadosRegistroAtendimento dados) {
        service.registrarAtendimento(id, dados.informacoes());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Editar evolução do atendimento", description = "Substitui a evolução de um atendimento realizado, respeitando o prazo de edição definido pela regra de negócio.")
    @PutMapping("/{id}/evolucao")
    public ResponseEntity<Void> editarEvolucao(
            @PathVariable Long id, @RequestBody @Valid DadosRegistroAtendimento dados) {
        service.editarEvolucao(id, dados.informacoes());
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Listar histórico de atendimentos", description = "Retorna somente sessões realizadas, com paciente, psicólogo, valor, prontuário e evolução, ordenadas da mais recente para a mais antiga.")
    @GetMapping("/historico")
    public ResponseEntity<Page<DadosHistoricoAtendimento>> historico(
            @PageableDefault(size = 20, sort = "data", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable paginacao) {
        return ResponseEntity.ok(service.listarHistorico(paginacao));
    }
    @Operation(summary = "Gerar resumo de consultas por período", description = "Agrupa atendimentos por psicólogo e paciente e retorna quantidade de consultas e valor total entre início e fim.")
    @GetMapping("/relatorio")
    public ResponseEntity<List<DadosRelatorioConsultaMensal>> relatorio(
            @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(service.gerarRelatorioMensal(inicio, fim));
    }
    @Operation(summary = "Gerar detalhes de consultas por período", description = "Retorna os dados detalhados dos atendimentos realizados entre início e fim para composição de relatórios.")
    @GetMapping("/relatorio/detalhes")
    public ResponseEntity<List<DadosRelatorioConsultaMensal>> relatorioDetalhes(
            @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(service.gerarRelatorioDetalhesMensal(inicio, fim));
    }
}
