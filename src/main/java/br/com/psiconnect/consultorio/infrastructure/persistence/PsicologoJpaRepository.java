package br.com.psiconnect.consultorio.infrastructure.persistence;

import br.com.psiconnect.consultorio.application.port.PsicologoRepository;

import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsicologoJpaRepository extends PsicologoRepository, JpaRepository<Psicologo,Long> {
    boolean existsByCrp(String crp);

    List<Psicologo> findAllByEspecialidadeOrderByIdAsc(Especialidade especialidade);

    List<Psicologo> findByNomeContainingIgnoreCase(@Param("nome") String nome);

}
