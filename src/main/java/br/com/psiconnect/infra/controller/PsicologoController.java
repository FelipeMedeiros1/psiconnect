package br.com.psiconnect.infra.controller;

import br.com.psiconnect.consultorio.psicologo.PsicologoService;
import br.com.psiconnect.consultorio.psicologo.dto.DadosAtualizacaoPsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosCadastroPsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosDetalhePsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosListagemPsicologo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/psicologos")
public class PsicologoController {

    private final PsicologoService psicologoService;

    public PsicologoController(PsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhePsicologo> cadastrar(@RequestBody @Valid DadosCadastroPsicologo dados, UriComponentsBuilder uriBuilder) {
        var psicologoCadastrado = psicologoService.cadastrar(dados);
        var uri = uriBuilder.path("/psicologos/{id}").buildAndExpand(psicologoCadastrado.id()).toUri();
        return ResponseEntity.created(uri).body(psicologoCadastrado);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPsicologo>> listar(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return ResponseEntity.ok(psicologoService.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhePsicologo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(psicologoService.buscarPorId(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhePsicologo>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(psicologoService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPsicologo dados) {
        if (!id.equals(dados.id())) {
            return ResponseEntity.badRequest().build();
        }
        psicologoService.atualizar(id, dados);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        psicologoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/desativar")
    @Transactional
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        psicologoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/inativar")
    @Transactional
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        psicologoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
