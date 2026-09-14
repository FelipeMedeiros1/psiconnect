package br.com.psiconnect.consultorio.infrastructure.web;

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

@RestController
@RequestMapping("/sessoes")
public class SessaoController {
    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosDetalhamentoSessao> agendar(
            @RequestBody @Valid DadosAgendamentoSessao dados, UriComponentsBuilder uriBuilder) {
        var sessao = service.agendar(dados);
        var uri = uriBuilder.path("/sessoes/{id}").buildAndExpand(sessao.id()).toUri();
        return ResponseEntity.created(uri).body(sessao);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemSessao>> listar(
            @PageableDefault(size = 20, sort = {"compareceu", "data"}) Pageable paginacao) {
        return ResponseEntity.ok(service.listarSessoes(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoSessao> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoSessao> atualizar(
            @PathVariable Long id, @RequestBody @Valid DadosAtualizacaoSessao dados) {
        if (!id.equals(dados.id())) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.atualizar(dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/atendimento")
    public ResponseEntity<Void> registrarAtendimento(
            @PathVariable Long id, @RequestBody @Valid DadosRegistroAtendimento dados) {
        service.registrarAtendimento(id, dados.informacoes());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/evolucao")
    public ResponseEntity<Void> editarEvolucao(
            @PathVariable Long id, @RequestBody @Valid DadosRegistroAtendimento dados) {
        service.editarEvolucao(id, dados.informacoes());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historico")
    public ResponseEntity<Page<DadosHistoricoAtendimento>> historico(
            @PageableDefault(size = 20, sort = "data", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable paginacao) {
        return ResponseEntity.ok(service.listarHistorico(paginacao));
    }

    @GetMapping("/relatorio")
    public ResponseEntity<List<DadosRelatorioConsultaMensal>> relatorio(
            @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(service.gerarRelatorioMensal(inicio, fim));
    }

    @GetMapping("/relatorio/detalhes")
    public ResponseEntity<List<DadosRelatorioConsultaMensal>> relatorioDetalhes(
            @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(service.gerarRelatorioDetalhesMensal(inicio, fim));
    }
}
