package br.com.psiconnect.consultorio.application.consulta.agendamento;

import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPsicologoAtivo implements ValidadorAgendamentoConsulta {

    private final PsicologoRepository repository;

    public ValidadorPsicologoAtivo(PsicologoRepository repository) {
        this.repository = repository;
    }

    public void validar(DadosAgendamentoSessao dados) {
        //escolha do psicologo opcional
        if (dados.idPsicologo() == null) {
            return;
        }

        var psicologoEstaAtivo = repository.findAtivoById(dados.idPsicologo());
        if (!psicologoEstaAtivo) {
            throw new ConsultorioException("Consulta não pode ser agendada com psicólogo inativo!");
        }
    }

}