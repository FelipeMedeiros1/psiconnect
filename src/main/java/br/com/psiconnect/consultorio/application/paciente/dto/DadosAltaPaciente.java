package br.com.psiconnect.consultorio.application.paciente.dto;

import jakarta.validation.constraints.NotBlank;

public record DadosAltaPaciente(@NotBlank String motivo, String usuario) {
}
