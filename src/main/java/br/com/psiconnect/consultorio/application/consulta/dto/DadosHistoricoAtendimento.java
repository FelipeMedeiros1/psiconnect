package br.com.psiconnect.consultorio.application.consulta.dto;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record DadosHistoricoAtendimento(
        Long id, LocalDateTime data, Long psicologoId, String psicologo, Long pacienteId,
        String paciente, BigDecimal valorSessao, String historico) {
    public DadosHistoricoAtendimento(Sessao sessao) {
        this(sessao.getId(), sessao.getData(), sessao.getPsicologo().getId(), sessao.getPsicologo().getNome(),
                sessao.getPaciente().getId(), sessao.getPaciente().getNome(), sessao.getValorSessao(),
                sessao.getProntuario());
    }
}
