package br.com.psiconnect.consultorio.application.psicologo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import br.com.psiconnect.consultorio.application.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.application.endereco.dto.DadosEndereco;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;

public record DadosCadastroPsicologo(
        @NotBlank
        String nome,
        DadosContato contato,
        @NotBlank
        @Pattern(regexp = "\\d{4,7}")
        String crp,
        @NotNull
        Especialidade especialidade,
        @NotNull @Valid DadosEndereco endereco
) {
}
