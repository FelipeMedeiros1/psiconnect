package br.com.psiconnect.consultorio.contato.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosContato(
        @NotBlank
        @Pattern(regexp = "^(\\(\\d{2}\\)|\\d{2})\\s?\\d{4,5}-?\\d{4}$")
        String telefone,
        @Email
        @NotBlank
        String email
) {
}
