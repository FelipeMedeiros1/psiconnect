package br.com.psiconnect.consultorio.application.consulta.dto;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;

import java.time.LocalDateTime;

public record DadosListagemSessao(Long id, LocalDateTime data, String psicologo, String paciente,
                                  String prontuarioPaciente,
                                  boolean compareceu, String evolucao) {

    public DadosListagemSessao(Sessao sessao) {
        this(sessao.getId(), sessao.getData(), sessao.getPsicologo().getNome(), sessao.getPaciente().getNome(),
                sessao.getPaciente().getProntuario(),
                sessao.isCompareceu(), sessao.isCompareceu() ? sessao.getEvolucao() : null);
    }

}
