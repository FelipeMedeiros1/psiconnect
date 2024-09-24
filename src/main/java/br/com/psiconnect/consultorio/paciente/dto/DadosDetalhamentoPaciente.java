package br.com.psiconnect.consultorio.paciente.dto;

import br.com.psiconnect.consultorio.paciente.Paciente;

import java.math.BigDecimal;

public record DadosDetalhamentoPaciente(Long id, Boolean ativo, String nome, String email, String cpf, String telefone,
                                        BigDecimal valorConsulta, long sessoesRealizadas) {

    public DadosDetalhamentoPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getAtivo(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf(), paciente.getContato().getTelefone(), paciente.getValorSessao(), paciente.calcularSessoesRealizadas());
    }
}