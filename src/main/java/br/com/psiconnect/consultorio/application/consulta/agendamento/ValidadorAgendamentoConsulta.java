package br.com.psiconnect.consultorio.application.consulta.agendamento;

import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;

public interface ValidadorAgendamentoConsulta {

    void validar(DadosAgendamentoSessao dados);

}
