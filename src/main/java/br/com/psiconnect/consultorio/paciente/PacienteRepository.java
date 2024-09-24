package br.com.psiconnect.consultorio.paciente;

import br.com.psiconnect.consultorio.consulta.Sessao;
import br.com.psiconnect.consultorio.paciente.dto.DadosDetalhamentoPaciente;
import br.com.psiconnect.consultorio.psicologo.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @Query("""
            select p.ativo
            from Paciente p
            where
            p.id = :id
            """)
    boolean findAtivoById(Long id);
    @Query("select p from Paciente p where lower(p.nome) like %:nome%")
    List<DadosDetalhamentoPaciente> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    boolean existsByCpf(String cpf);

    List<Sessao> findAllByPacienteIdAndDataBetween(Long id, LocalDateTime inicioMes, LocalDateTime fimMes);
}
