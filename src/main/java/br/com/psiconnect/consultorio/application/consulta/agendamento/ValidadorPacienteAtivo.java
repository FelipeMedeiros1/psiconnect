package br.com.psiconnect.consultorio.application.consulta.agendamento;

import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.application.port.PacienteRepository;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteAtivo implements ValidadorAgendamentoConsulta {

    private final PacienteRepository repository;

    public ValidadorPacienteAtivo(PacienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(DadosAgendamentoSessao dados) {
        var pacienteEstaAtivo = repository.findStatusById(dados.idPaciente());
        if (!pacienteEstaAtivo) {
            throw new ConsultorioException("Consulta não pode ser agendada! Paciente de alta");
        }
    }

}