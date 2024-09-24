package br.com.psiconnect.consultorio.consulta.dto;

import br.com.psiconnect.consultorio.consulta.Sessao;

import java.time.LocalDateTime;

public record DadosListagemSessao(Long id, LocalDateTime data, String psicologo, String paciente) {

    public DadosListagemSessao(Sessao sessao) {
        this(sessao.getId(), sessao.getData(), sessao.getPsicologo().getNome(), sessao.getPaciente().getNome());
    }

}
