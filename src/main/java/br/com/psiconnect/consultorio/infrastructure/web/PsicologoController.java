package br.com.psiconnect.consultorio.infrastructure.web;

import br.com.psiconnect.consultorio.application.psicologo.PsicologoService;
import br.com.psiconnect.consultorio.application.psicologo.dto.DadosAtualizacaoPsicologo;
import br.com.psiconnect.consultorio.application.psicologo.dto.DadosCadastroPsicologo;
import br.com.psiconnect.consultorio.application.psicologo.dto.DadosDetalhePsicologo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/psicologos")
public class PsicologoController {
    private final PsicologoService service;

    public PsicologoController(PsicologoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosDetalhePsicologo> cadastrar(
            @RequestBody @Valid DadosCadastroPsicologo dados, UriComponentsBuilder uriBuilder) {
        var detalhe = new DadosDetalhePsicologo(service.cadastrar(dados));
        var uri = uriBuilder.path("/psicologos/{id}").buildAndExpand(detalhe.id()).toUri();
        return ResponseEntity.created(uri).body(detalhe);
    }

    @GetMapping
    public ResponseEntity<Page<DadosDetalhePsicologo>> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable paginacao) {
        return ResponseEntity.ok(service.consultar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhePsicologo> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new DadosDetalhePsicologo(service.buscarPorId(id)));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhePsicologo>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPsicologo dados) {
        if (!id.equals(dados.id())) return ResponseEntity.badRequest().build();
        service.atualizar(id, dados);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
