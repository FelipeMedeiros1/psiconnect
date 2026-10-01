package br.com.psiconnect.consultorio.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import br.com.psiconnect.consultorio.application.localatendimento.LocalAtendimentoService;
import br.com.psiconnect.consultorio.application.localatendimento.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Locais de atendimento", description = "Cadastro e manutenção dos consultórios e modalidades onde os pacientes são atendidos.")
@RestController @RequestMapping("/locais-atendimento")
public class LocalAtendimentoController {
    private final LocalAtendimentoService service;
    public LocalAtendimentoController(LocalAtendimentoService service) { this.service = service; }
    @Operation(summary = "Cadastrar local de atendimento", description = "Cria um consultório ou modalidade de atendimento com nome e endereço opcional.")
    @PostMapping public ResponseEntity<DetalheLocalAtendimento> cadastrar(@RequestBody @Valid DadosLocalAtendimento dados) { return ResponseEntity.ok(service.cadastrar(dados)); }
    @Operation(summary = "Listar locais de atendimento", description = "Retorna os locais cadastrados em ordem alfabética, incluindo situação e endereço.")
    @GetMapping public Page<DetalheLocalAtendimento> listar(@PageableDefault(size=100, sort="nomeLugar") Pageable pageable) { return service.listar(pageable); }
    @Operation(summary = "Consultar local por ID", description = "Retorna os dados do local de atendimento identificado pelo ID.")
    @GetMapping("/{id}") public DetalheLocalAtendimento buscar(@PathVariable Long id) { return service.buscar(id); }
    @Operation(summary = "Atualizar local de atendimento", description = "Altera o nome e o endereço do local identificado pelo ID.")
    @PutMapping("/{id}") public DetalheLocalAtendimento atualizar(@PathVariable Long id, @RequestBody @Valid DadosLocalAtendimento dados) { return service.atualizar(id, dados); }
    @Operation(summary = "Inativar local de atendimento", description = "Inativa o local sem apagar seu histórico ou os vínculos existentes com pacientes.")
    @DeleteMapping("/{id}") public ResponseEntity<Void> inativar(@PathVariable Long id) { service.inativar(id); return ResponseEntity.noContent().build(); }
}
