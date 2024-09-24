package br.com.psiconnect.consultorio.consulta;


import br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.List;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    boolean existsByPacienteIdAndDataBetween(Long idPaciente, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);

    boolean existsByPsicologoIdAndData(Long idPsicologo, LocalDateTime data);

    Page<Sessao> findAllByDataGreaterThan(LocalDateTime data, Pageable paginacao);

    @Query("""
             select new br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal(p.nome, p.crp, COUNT(s))
                     from Sessao s join s.psicologo p
                     where s.data >= :inicioMes and s.data <= :fimMes
                     group by p.nome, p.crp
            """)
    List<DadosRelatorioConsultaMensal> gerarRelatorioConsultaMensal(LocalDateTime inicioMes, LocalDateTime fimMes);
    @Query("""
             select new br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal(p.nome, p.crp, COUNT(s), s.paciente.nome, SUM(s.valorConsulta))
                     from Sessao s join s.psicologo p
                     where s.data >= :inicioMes and s.data <= :fimMes
                     group by p.nome, p.crp, s.paciente.nome
            """)
    List<DadosRelatorioConsultaMensal> gerarRelatorioDetalhesMensal(LocalDateTime inicioMes, LocalDateTime fimMes);

}
