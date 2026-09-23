package br.com.psiconnect.consultorio.application.paciente.dto;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;

public record DadosListagemPaciente(Long id, Boolean status, String nome, String email, String cpf, String numeroProntuario) {
    public DadosListagemPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getStatus(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf(),
                paciente.getProntuario().lines().findFirst().orElse(""));
    }
}