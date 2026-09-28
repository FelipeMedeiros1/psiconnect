package br.com.psiconnect.consultorio.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Psicólogos", description = "Cadastro, pesquisa, atualização e controle de disponibilidade dos psicólogos.")
@RestController
@RequestMapping("/psicologos")
public class PsicologoController {
    private final PsicologoService service;

    public PsicologoController(PsicologoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar psicólogo", description = "Cria um psicólogo com nome, CRP, especialidade, contato e endereço. Retorna o cadastro criado e o endereço do recurso.")
    @PostMapping
    public ResponseEntity<DadosDetalhePsicologo> cadastrar(
            @RequestBody @Valid DadosCadastroPsicologo dados, UriComponentsBuilder uriBuilder) {
        var detalhe = new DadosDetalhePsicologo(service.cadastrar(dados));
        var uri = uriBuilder.path("/psicologos/{id}").buildAndExpand(detalhe.id()).toUri();
        return ResponseEntity.created(uri).body(detalhe);
    }

    @Operation(summary = "Listar psicólogos", description = "Retorna uma página de psicólogos ordenada por nome. Aceita os parâmetros page, size e sort.")
    @GetMapping
    public ResponseEntity<Page<DadosDetalhePsicologo>> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable paginacao) {
        return ResponseEntity.ok(service.consultar(paginacao));
    }
    @Operation(summary = "Consultar psicólogo por ID", description = "Retorna os dados completos do psicólogo identificado pelo ID.")
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhePsicologo> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new DadosDetalhePsicologo(service.buscarPorId(id)));
    }
    @Operation(summary = "Pesquisar psicólogos por nome", description = "Localiza psicólogos cujo nome contenha o texto informado, sem diferenciar maiúsculas e minúsculas.")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhePsicologo>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @Operation(summary = "Atualizar psicólogo", description = "Atualiza os dados do psicólogo. O ID da URL deve corresponder ao ID enviado no corpo.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPsicologo dados) {
        if (!id.equals(dados.id())) return ResponseEntity.badRequest().build();
        service.atualizar(id, dados);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desativar psicólogo", description = "Marca o psicólogo como inativo e impede que ele seja usado em novos agendamentos.")
    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reativar psicólogo", description = "Reativa o psicólogo para que volte a aparecer como disponível em novos agendamentos.")
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        service.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir psicólogo", description = "Exclui o psicólogo identificado pelo ID quando não houver impedimento pelas regras de negócio.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
