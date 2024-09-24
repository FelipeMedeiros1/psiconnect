package br.com.psiconnect.infra.controller;

import br.com.psiconnect.consultorio.paciente.Paciente;
import br.com.psiconnect.consultorio.paciente.PacienteService;
import br.com.psiconnect.consultorio.paciente.dto.*;
import br.com.psiconnect.infra.exception.ConsultorioException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoPaciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uriBuilder) {
        try {
            var pacienteCadastrado = pacienteService.cadastrar(dados);
            var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteCadastrado.id()).toUri();
            return ResponseEntity.created(uri).body(pacienteCadastrado);
        } catch (ConsultorioException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    ResponseEntity<Page<DadosListagemPaciente>> listar(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao){
        return ResponseEntity.ok(pacienteService.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoPaciente> buscarPorId(@PathVariable Long id) {
        try {
            DadosDetalhamentoPaciente paciente = pacienteService.buscarPorId(id);
            return ResponseEntity.ok(paciente);
        } catch (ConsultorioException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhamentoPaciente>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(pacienteService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoPaciente> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPaciente dados) {
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
            @PathVariable String inicioMes,
            @PathVariable String fimMes) {
        LocalDateTime inicio = LocalDateTime.parse(inicioMes,  DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime fim = LocalDateTime.parse(fimMes, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ResponseEntity.ok(pacienteService.gerarRelatorioMensal(inicio, fim));
    }
}