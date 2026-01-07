package br.com.psiconnect.infra.controller;

import br.com.psiconnect.consultorio.consulta.SessaoService;
import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosAtualizacaoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosCancelamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosAtualizacaoValorSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosDetalhamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosListagemSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoSessao> agendar(@RequestBody @Valid DadosAgendamentoSessao dados) {
        return ResponseEntity.ok(sessaoService.agendar(dados));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemSessao>> listar(@PageableDefault(size = 20, sort = {"data"}) Pageable paginacao) {
        return ResponseEntity.ok(sessaoService.listarSessoes(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoSessao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sessaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoSessao dados) {
        if (!id.equals(dados.id())) {
            return ResponseEntity.badRequest().build();
        }
        sessaoService.atualizar(id, dados);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        sessaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/inativar")
    @Transactional
    public ResponseEntity<Void> inativar(@PathVariable Long id, @RequestBody @Valid DadosCancelamentoSessao dados) {
        sessaoService.inativar(id, dados);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio/{inicioMes}/{fimMes}")
    public ResponseEntity<List<DadosRelatorioConsultaMensal>> gerarRelatorioMensal(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioMes,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimMes) {
        return ResponseEntity.ok(sessaoService.gerarRelatorioMensal(inicioMes.atStartOfDay(), fimMes.atTime(LocalTime.MAX)));
    }

    @GetMapping("/relatorio-detalhado/{inicioMes}/{fimMes}")
    public ResponseEntity<List<DadosRelatorioConsultaMensal>> gerarRelatorioDetalhadoMensal(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioMes,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimMes) {
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fim = fimMes.atTime(LocalTime.MAX);
        return ResponseEntity.ok(sessaoService.gerarRelatorioDetalhesMensal(inicio, fim));
    }

    @PutMapping("/valores")
    @Transactional
    public ResponseEntity<Void> atualizarValoresSessao(@RequestBody @Valid DadosAtualizacaoValorSessao dados) {
        sessaoService.atualizarValoresSessao(dados);
        return ResponseEntity.noContent().build();
    }
}
