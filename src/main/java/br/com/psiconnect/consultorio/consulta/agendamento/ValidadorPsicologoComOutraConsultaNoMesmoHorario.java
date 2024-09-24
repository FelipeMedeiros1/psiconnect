package br.com.psiconnect.consultorio.consulta.agendamento;


import br.com.psiconnect.consultorio.consulta.SessaoRepository;
import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ValidadorPsicologoComOutraConsultaNoMesmoHorario implements ValidadorAgendamentoConsulta {

    @Autowired
    private SessaoRepository repository;

    public void validar(DadosAgendamentoSessao dados) {
        var psicologoPossuiOutraConsultaNoMesmoHorario = repository.existsByPsicologoIdAndData(dados.idPsicologo(), dados.data());
        if (psicologoPossuiOutraConsultaNoMesmoHorario) {
            throw new ConsultorioException("Psicólogo já possui outra consulta agendada nesse mesmo horário");
        }
    }

}
