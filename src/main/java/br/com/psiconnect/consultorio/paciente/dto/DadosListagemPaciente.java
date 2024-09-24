package br.com.psiconnect.consultorio.paciente.dto;


import br.com.psiconnect.consultorio.paciente.Paciente;

public record DadosListagemPaciente(Long id, Boolean ativo, String nome, String email, String cpf) {
    public DadosListagemPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getAtivo(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf());
    }
}
