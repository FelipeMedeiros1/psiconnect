package br.com.psiconnect.consultorio.application.localatendimento.dto;

import br.com.psiconnect.consultorio.application.endereco.dto.DadosEndereco;
import jakarta.validation.constraints.NotBlank;

public record DadosLocalAtendimento(@NotBlank String nomeLugar, DadosEndereco endereco) {
}
