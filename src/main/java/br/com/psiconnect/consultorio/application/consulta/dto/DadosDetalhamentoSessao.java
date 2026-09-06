package br.com.psiconnect.consultorio.application.consulta.dto;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;

import java.time.LocalDateTime;

public record DadosDetalhamentoSessao(Long id, Long idPsicologo, Long idPaciente, LocalDateTime data) {

    public DadosDetalhamentoSessao(Sessao sessao) {
        this(sessao.getId(), sessao.getPsicologo().getId(), sessao.getPaciente().getId(), sessao.getData());
    }
}