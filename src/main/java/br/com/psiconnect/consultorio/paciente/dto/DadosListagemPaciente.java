package br.com.psiconnect.consultorio.paciente.dto;


import br.com.psiconnect.consultorio.paciente.Paciente;

public record DadosListagemPaciente(Long id, Boolean status, String nome, String email, String cpf) {
    public DadosListagemPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getStatus(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf());
    }
}
