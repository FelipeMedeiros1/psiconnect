package br.com.psiconnect.infra.controller;

import br.com.psiconnect.consultorio.financeiro.FinanceiroService;
import br.com.psiconnect.consultorio.financeiro.dto.DadosCadastroDespesa;
import br.com.psiconnect.consultorio.financeiro.dto.DadosDetalheDespesa;
import br.com.psiconnect.consultorio.financeiro.dto.DadosResumoFinanceiro;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @PostMapping("/despesas")
    @Transactional
    public ResponseEntity<DadosDetalheDespesa> cadastrarDespesa(@RequestBody @Valid DadosCadastroDespesa dados) {
        return ResponseEntity.ok(financeiroService.cadastrarDespesa(dados));
    }

    @GetMapping("/despesas")
    public ResponseEntity<List<DadosDetalheDespesa>> listarDespesas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioMes,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimMes) {
        return ResponseEntity.ok(financeiroService.listarDespesas(inicioMes, fimMes));
    }

    @GetMapping("/resumo")
    public ResponseEntity<DadosResumoFinanceiro> gerarResumo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioMes,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimMes) {
        return ResponseEntity.ok(financeiroService.gerarResumo(inicioMes, fimMes));
    }
}
