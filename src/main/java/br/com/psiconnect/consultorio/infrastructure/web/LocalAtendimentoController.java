package br.com.psiconnect.consultorio.infrastructure.web;

import br.com.psiconnect.consultorio.application.localatendimento.LocalAtendimentoService;
import br.com.psiconnect.consultorio.application.localatendimento.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/locais-atendimento")
public class LocalAtendimentoController {
    private final LocalAtendimentoService service;
    public LocalAtendimentoController(LocalAtendimentoService service) { this.service = service; }
    @PostMapping public ResponseEntity<DetalheLocalAtendimento> cadastrar(@RequestBody @Valid DadosLocalAtendimento dados) { return ResponseEntity.ok(service.cadastrar(dados)); }
    @GetMapping public Page<DetalheLocalAtendimento> listar(@PageableDefault(size=100, sort="nomeLugar") Pageable pageable) { return service.listar(pageable); }
    @GetMapping("/{id}") public DetalheLocalAtendimento buscar(@PathVariable Long id) { return service.buscar(id); }
    @PutMapping("/{id}") public DetalheLocalAtendimento atualizar(@PathVariable Long id, @RequestBody @Valid DadosLocalAtendimento dados) { return service.atualizar(id, dados); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> inativar(@PathVariable Long id) { service.inativar(id); return ResponseEntity.noContent().build(); }
}
