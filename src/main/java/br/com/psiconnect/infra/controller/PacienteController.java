package br.com.psiconnect.infra.controller;

import br.com.psiconnect.consultorio.paciente.PacienteService;
import br.com.psiconnect.consultorio.paciente.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhePaciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uriBuilder) {
        var pacienteCadastrado = pacienteService.cadastrar(dados);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteCadastrado.id()).toUri();
        return ResponseEntity.created(uri).body(pacienteCadastrado);
    }

    @GetMapping
    ResponseEntity<Page<DadosListagemPaciente>> listar(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao){
        return ResponseEntity.ok(pacienteService.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhePaciente> buscarPorId(@PathVariable Long id) {
        DadosDetalhePaciente paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhePaciente>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(pacienteService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhePaciente> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPaciente dados) {
        if (!id.equals(dados.id())) {
            return ResponseEntity.badRequest().build();
        }
        var pacienteAtualizado = pacienteService.atualizar(dados);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/alta")
    public ResponseEntity<Void> altaPaciente(@RequestBody DadosAtualizacaoPaciente dados) {
        pacienteService.altaPaciente(dados);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio/{inicioMes}/{fimMes}")
    public ResponseEntity<List<DadosRelatorioPacienteMensal>> gerarRelatorioMensal(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioMes,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimMes) {
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fim = fimMes.atTime(LocalTime.MAX);
        return ResponseEntity.ok(pacienteService.gerarRelatorioMensal(inicio, fim));
    }
}
