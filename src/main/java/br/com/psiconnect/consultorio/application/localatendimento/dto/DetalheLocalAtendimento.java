package br.com.psiconnect.consultorio.application.localatendimento.dto;

import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.localatendimento.LocalAtendimento;

public record DetalheLocalAtendimento(Long id, String nomeLugar, Endereco endereco, boolean ativo) {
    public DetalheLocalAtendimento(LocalAtendimento local) {
        this(local.getId(), local.getNomeLugar(), local.getEndereco(), local.isAtivo());
    }
}
