package br.com.psiconnect.consultorio.application.paciente.dto;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;

import java.time.LocalDate;
import java.time.Period;

public record DadosListagemPaciente(Long id, Boolean status, String nome, String email, String cpf,
                                    String numeroProntuario, String telefone, Integer idade) {
    public DadosListagemPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getStatus(), paciente.getNome(), paciente.getContato().getEmail(), paciente.getCpf(),
                paciente.getProntuario().lines().findFirst().orElse(""), paciente.getContato().getTelefone(),
                Period.between(paciente.getDataNascimento(), LocalDate.now()).getYears());
    }
}
