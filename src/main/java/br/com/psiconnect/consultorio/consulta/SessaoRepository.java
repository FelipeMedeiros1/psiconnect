package br.com.psiconnect.consultorio.consulta;


import br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    boolean existsByPacienteIdAndDataBetween(Long idPaciente, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);

    boolean existsByPsicologoIdAndData(Long idPsicologo, LocalDateTime data);

    List<Sessao> findAllByPaciente_IdAndDataBetween(Long pacienteId, LocalDateTime inicioMes, LocalDateTime fimMes);

    Page<Sessao> findAllByDataGreaterThan(LocalDateTime data, Pageable paginacao);

    List<Sessao> findAllByDataBetweenAndCompareceuTrue(LocalDateTime inicioMes, LocalDateTime fimMes);

    @Query("SELECT s FROM Sessao s WHERE s.data BETWEEN :inicioMes AND :fimMes")
    List<Sessao> gerarRelatorioConsultaMensal(@Param("inicioMes") LocalDateTime inicioMes, @Param("fimMes") LocalDateTime fimMes);


    @Query("""
                 select new br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal(p.nome, p.crp, COUNT(s), s.paciente.nome, SUM(s.valorSessao))
                 from Sessao s join s.psicologo p
                 where s.data >= :inicioMes and s.data <= :fimMes
                 group by p.nome, p.crp, s.paciente.nome
            """)
    List<DadosRelatorioConsultaMensal> gerarRelatorioDetalhesMensal(@Param("inicioMes") LocalDateTime inicioMes, @Param("fimMes") LocalDateTime fimMes);

}
