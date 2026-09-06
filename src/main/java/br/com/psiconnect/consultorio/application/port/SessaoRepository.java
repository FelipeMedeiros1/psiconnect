package br.com.psiconnect.consultorio.application.port;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosRelatorioConsultaMensal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface SessaoRepository {
    <S extends Sessao> S save(S entidade);
    Optional<Sessao> findById(Long id);
    Sessao getReferenceById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<Sessao> findAll();
    Page<Sessao> findAll(Pageable paginacao);
    boolean existsByPacienteIdAndDataBetween(Long idPaciente, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);
    boolean existsByPsicologoIdAndData(Long idPsicologo, LocalDateTime data);
    List<Sessao> findAllByPaciente_IdAndDataBetween(Long pacienteId, LocalDateTime inicioMes, LocalDateTime fimMes);
    Page<Sessao> findAllByDataGreaterThan(LocalDateTime data, Pageable paginacao);
    List<Sessao> gerarRelatorioConsultaMensal(LocalDateTime inicioMes, LocalDateTime fimMes);
    List<DadosRelatorioConsultaMensal> gerarRelatorioDetalhesMensal(LocalDateTime inicioMes, LocalDateTime fimMes);
}
