package br.com.psiconnect.consultorio.paciente.dto;

import br.com.psiconnect.consultorio.paciente.Paciente;

import java.math.BigDecimal;

public record DadosDetalhePaciente(Long id, Boolean status, String nome, String email, String cpf, String telefone,
                                   BigDecimal valorConsulta, long sessoesRealizadas) {

    public DadosDetalhePaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getStatus(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf(), paciente.getContato().getTelefone(), paciente.getValorSessao(), paciente.calcularSessoesRealizadas());
    }
}