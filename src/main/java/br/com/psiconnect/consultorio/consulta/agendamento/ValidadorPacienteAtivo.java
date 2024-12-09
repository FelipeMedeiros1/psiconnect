package br.com.psiconnect.consultorio.consulta.agendamento;


import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.paciente.PacienteRepository;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ValidadorPacienteAtivo implements ValidadorAgendamentoConsulta {

    @Autowired
    private PacienteRepository repository;
    @Override
    public void validar(DadosAgendamentoSessao dados) {
        var pacienteEstaAtivo = repository.findStatusById(dados.idPaciente());
        if (!pacienteEstaAtivo) {
            throw new ConsultorioException("Consulta não pode ser agendada! Paciente de alta");
        }
    }


}
