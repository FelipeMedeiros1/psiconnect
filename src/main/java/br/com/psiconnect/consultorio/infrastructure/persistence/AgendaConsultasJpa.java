package br.com.psiconnect.consultorio.infrastructure.persistence;

import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.application.port.SessaoRepository;
import br.com.psiconnect.consultorio.domain.consulta.AgendaConsultas;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AgendaConsultasJpa implements AgendaConsultas {
    private final PsicologoRepository psicologos;
    private final SessaoRepository sessoes;

    public AgendaConsultasJpa(PsicologoRepository psicologos, SessaoRepository sessoes) {
        this.psicologos = psicologos;
        this.sessoes = sessoes;
    }

    @Override
    public boolean horarioOcupado(Long psicologoId, LocalDateTime data) {
        return sessoes.existsByPsicologoIdAndData(psicologoId, data);
    }

    @Override
    public List<Psicologo> psicologosDaEspecialidade(Especialidade especialidade) {
        return psicologos.findAllByEspecialidadeOrderByIdAsc(especialidade);
    }
}
