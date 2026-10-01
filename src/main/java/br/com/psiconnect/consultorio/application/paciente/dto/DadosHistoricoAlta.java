package br.com.psiconnect.consultorio.application.paciente.dto;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.paciente.AltaPaciente;
import java.time.LocalDateTime;

public record DadosHistoricoAlta(Long pacienteId, String paciente, LocalDateTime data,
                                  String usuario, String motivo) {
    public DadosHistoricoAlta(Paciente paciente) {
        this(paciente.getId(), paciente.getNome(), paciente.getDataAlta(),
                paciente.getUsuarioAlta(), paciente.getMotivoAlta());
    }

    public DadosHistoricoAlta(Paciente paciente, AltaPaciente alta) {
        this(paciente.getId(), paciente.getNome(), alta.getData(), alta.getUsuario(), alta.getMotivo());
    }
}
