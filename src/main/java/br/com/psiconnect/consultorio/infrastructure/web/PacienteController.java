package br.com.psiconnect.consultorio.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import br.com.psiconnect.consultorio.application.paciente.PacienteService;
import br.com.psiconnect.consultorio.application.paciente.dto.*;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "Pacientes", description = "Cadastro, consulta, atualização, alta, reativação e relatórios de pacientes.")
@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteService pacienteService;

    @Operation(summary = "Cadastrar paciente", description = "Cria um paciente com dados pessoais, contato, endereço, responsável quando menor e local de atendimento. Retorna o cadastro completo e o endereço do novo recurso.")
    @PostMapping
    public ResponseEntity<DadosDetalhePaciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uriBuilder) {
        try {
            var pacienteCadastrado = pacienteService.cadastrar(dados);
            var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteCadastrado.id()).toUri();
            return ResponseEntity.created(uri).body(pacienteCadastrado);
        } catch (ConsultorioException e) {
            log.warn("event=request_rejected reason=patient_registration_rule status=400");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Listar pacientes", description = "Retorna uma página de pacientes ordenada por prontuário mais recente. Aceita os parâmetros page, size e sort do Spring Data.")
    @GetMapping
    ResponseEntity<Page<DadosListagemPaciente>> listar(
            @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable paginacao){
        return ResponseEntity.ok(pacienteService.listar(paginacao));
    }
    @Operation(summary = "Consultar paciente por ID", description = "Retorna todos os dados cadastrais, contato, endereço, prontuário, situação e local de atendimento do paciente informado.")
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhePaciente> buscarPorId(@PathVariable Long id) {
        try {
            DadosDetalhePaciente paciente = pacienteService.buscarPorId(id);
            return ResponseEntity.ok(paciente);
        } catch (ConsultorioException e) {
            log.warn("event=request_rejected reason=patient_not_found status=404");
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Pesquisar pacientes por nome", description = "Localiza pacientes cujo nome contenha o texto informado, sem diferenciar letras maiúsculas e minúsculas.")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<DadosDetalhePaciente>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(pacienteService.buscarPorNome(nome));
    }

    @Operation(summary = "Atualizar paciente", description = "Atualiza os dados cadastrais do paciente. O ID da URL deve ser igual ao ID enviado no corpo da requisição.")
    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhePaciente> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPaciente dados) {
        if (!id.equals(dados.id())) {
            log.warn("event=request_rejected reason=patient_id_mismatch status=400");
            return ResponseEntity.badRequest().build();
        }
        var pacienteAtualizado = pacienteService.atualizar(dados);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @Operation(summary = "Associar local de atendimento", description = "Define ou remove o local de atendimento vinculado ao paciente. Envie localAtendimentoId nulo para remover a associação.")
    @PutMapping("/{id}/local-atendimento")
    public ResponseEntity<DadosDetalhePaciente> associarLocal(
            @PathVariable Long id, @RequestBody DadosLocalPaciente dados) {
        return ResponseEntity.ok(pacienteService.associarLocal(id, dados.localAtendimentoId()));
    }

    @Operation(summary = "Excluir paciente", description = "Exclui o paciente identificado pelo ID quando as regras de negócio permitirem.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Registrar alta do paciente", description = "Encerra o acompanhamento, inativa o paciente e registra no histórico o motivo, a data e o usuário responsável pela alta.")
    @PutMapping("/{id}/alta")
    public ResponseEntity<Void> altaPaciente(@PathVariable Long id, @RequestBody @Valid DadosAltaPaciente dados) {
        pacienteService.altaPaciente(id, dados);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reativar paciente", description = "Reativa um paciente que recebeu alta, permitindo novos agendamentos e alterações cadastrais.")
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativarPaciente(@PathVariable Long id) {
        pacienteService.reativarPaciente(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Registrar alta pelo contrato legado", description = "Endpoint mantido para compatibilidade. Registra a alta usando o ID e o motivo presentes no corpo de atualização do paciente.", deprecated = true)
    @PutMapping("/alta")
    public ResponseEntity<Void> altaPacienteLegado(@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        pacienteService.altaPaciente(dados.id(), new DadosAltaPaciente(dados.motivoAlta(), "Sistema"));
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Listar histórico de altas", description = "Retorna todas as altas registradas, incluindo paciente, data, motivo e usuário responsável.")
    @GetMapping("/altas")
    public ResponseEntity<List<DadosHistoricoAlta>> listarAltas() {
        return ResponseEntity.ok(pacienteService.listarAltas());
    }
    @Operation(summary = "Gerar relatório mensal de pacientes", description = "Resume os atendimentos dos pacientes dentro do período informado. As datas devem usar o formato yyyy-MM-dd.")
    @GetMapping("/relatorio/{inicioMes}/{fimMes}")
    public ResponseEntity<List<DadosRelatorioPacienteMensal>> gerarRelatorioMensal(
            @PathVariable String inicioMes,
            @PathVariable String fimMes) {
        LocalDateTime inicio = LocalDateTime.parse(inicioMes,  DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime fim = LocalDateTime.parse(fimMes, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ResponseEntity.ok(pacienteService.gerarRelatorioMensal(inicio, fim));
    }
}
