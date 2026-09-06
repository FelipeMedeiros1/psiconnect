package br.com.psiconnect.consultorio.application.consulta.agendamento;

import br.com.psiconnect.consultorio.application.port.SessaoRepository;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPsicologoComOutraConsultaNoMesmoHorario implements ValidadorAgendamentoConsulta {

    private final SessaoRepository repository;

    public ValidadorPsicologoComOutraConsultaNoMesmoHorario(SessaoRepository repository) {
        this.repository = repository;
    }

    public void validar(DadosAgendamentoSessao dados) {
        var psicologoPossuiOutraConsultaNoMesmoHorario = repository.existsByPsicologoIdAndData(dados.idPsicologo(), dados.data());
        if (psicologoPossuiOutraConsultaNoMesmoHorario) {
            throw new ConsultorioException("Psicólogo já possui outra consulta agendada nesse mesmo horário");
        }
    }

}