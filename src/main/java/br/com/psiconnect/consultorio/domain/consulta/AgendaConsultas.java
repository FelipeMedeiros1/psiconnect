package br.com.psiconnect.consultorio.domain.consulta;

import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendaConsultas {
    boolean horarioOcupado(Long psicologoId, LocalDateTime data);

    List<Psicologo> psicologosDaEspecialidade(Especialidade especialidade);
}
