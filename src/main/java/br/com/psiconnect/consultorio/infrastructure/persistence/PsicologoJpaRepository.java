package br.com.psiconnect.consultorio.infrastructure.persistence;

import br.com.psiconnect.consultorio.application.port.PsicologoRepository;

import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PsicologoJpaRepository extends PsicologoRepository, JpaRepository<Psicologo,Long> {
    @Query("""
            select p.ativo
            from Psicologo p
            where
            p.id = :id
            """)
    boolean findAtivoById(Long id);

    boolean existsByCrp(String crp);

    @Query("SELECT p FROM Psicologo p WHERE p.especialidade = :especialidade AND p.id NOT IN (SELECT s.psicologo.id FROM Sessao s WHERE s.data = :data)")
    Psicologo escolherPsicologoLivreNaData(@Param("especialidade") Especialidade especialidade, @Param("data") LocalDateTime data);

    List<Psicologo> findByNomeContainingIgnoreCase(@Param("nome") String nome);

}