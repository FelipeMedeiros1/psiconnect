package br.com.psiconnect.consultorio.consulta.agendamento;


import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.paciente.PacienteRepository;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ValidadorPsicologoAtivo implements ValidadorAgendamentoConsulta {

    @Autowired
    private PacienteRepository repository;

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
