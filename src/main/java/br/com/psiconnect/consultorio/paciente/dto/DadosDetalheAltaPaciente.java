package br.com.psiconnect.consultorio.paciente.dto;

import br.com.psiconnect.consultorio.paciente.Paciente;

public record DadosDetalheAltaPaciente(Long id, Boolean status, String motivo, String nome, String email, String cpf) {

    public DadosDetalheAltaPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getStatus(),paciente.getMotivoAlta(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf());
    }
}