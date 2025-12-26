package br.com.psiconnect.infra.controller;

import br.com.psiconnect.consultorio.consulta.SessaoService;
import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
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
