package br.com.psiconnect.consultorio.infrastructure.persistence;

import br.com.psiconnect.consultorio.application.port.PacienteRepository;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PacienteJpaRepository extends PacienteRepository, JpaRepository<Paciente, Long> {
    @Query("""
            select p.status
            from Paciente p
            where
            p.id = :id
            """)
    boolean findStatusById(Long id);
    List<Paciente> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    boolean existsByCpf(String cpf);

}