package br.com.psiconnect.consultorio.paciente;

import br.com.psiconnect.consultorio.paciente.dto.DadosDetalhePaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @Query("""
            select p.status
            from Paciente p
            where
            p.id = :id
            """)
    boolean findStatusById(Long id);
    @Query("select p from Paciente p where lower(p.nome) like %:nome%")
    List<DadosDetalhePaciente> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    boolean existsByCpf(String cpf);


}
