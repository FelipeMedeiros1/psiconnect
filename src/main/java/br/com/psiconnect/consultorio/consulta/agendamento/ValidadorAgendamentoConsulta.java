package br.com.psiconnect.consultorio.consulta.agendamento;

import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;

public interface ValidadorAgendamentoConsulta {

    void validar(DadosAgendamentoSessao dados);

}
