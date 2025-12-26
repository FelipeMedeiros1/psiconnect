package br.com.psiconnect.consultorio.consulta.dto;

import br.com.psiconnect.consultorio.consulta.Sessao;
import br.com.psiconnect.consultorio.consulta.StatusSessao;

import java.time.LocalDateTime;

public record DadosListagemSessao(Long id, LocalDateTime data, String psicologo, String paciente, StatusSessao status) {

    public DadosListagemSessao(Sessao sessao) {
        this(sessao.getId(), sessao.getData(), sessao.getPsicologo().getNome(), sessao.getPaciente().getNome(), sessao.getStatus());
    }

}
