package br.com.psiconnect.consultorio.application.paciente.dto;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.paciente.Responsavel;
import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.application.localatendimento.dto.DetalheLocalAtendimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosDetalhePaciente(Long id, Boolean status, String nome, String email, String cpf, String telefone,
                                   BigDecimal valorConsulta, long sessoesRealizadas, LocalDate dataNascimento,
                                   String profissao, Responsavel responsavel, Contato contato, Endereco endereco,
                                   DetalheLocalAtendimento localAtendimento) {

    public DadosDetalhePaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getStatus(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf(), paciente.getContato().getTelefone(), paciente.getValorSessao(), paciente.calcularSessoesRealizadas(), paciente.getDataNascimento(), paciente.getProfissao(), paciente.getResponsavel(), paciente.getContato(), paciente.getEndereco(),
                paciente.getLocalAtendimento() == null ? null : new DetalheLocalAtendimento(paciente.getLocalAtendimento()));
    }
}
