package br.com.psiconnect.consultorio.application.consulta.dto;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record DadosDetalhamentoSessao(Long id, Long idPsicologo, Long idPaciente, LocalDateTime data, BigDecimal valorSessao) {

    public DadosDetalhamentoSessao(Sessao sessao) {
        this(sessao.getId(), sessao.getPsicologo().getId(), sessao.getPaciente().getId(), sessao.getData(), sessao.getValorSessao());
    }
}
