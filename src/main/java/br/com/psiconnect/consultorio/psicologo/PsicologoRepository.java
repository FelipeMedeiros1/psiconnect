package br.com.psiconnect.consultorio.psicologo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PsicologoRepository extends JpaRepository<Psicologo,Long> {
    @Query("""
            select p.ativo
            from Psicologo p
            where
            p.id = :id
            """)
    boolean findAtivoById(Long id);

    boolean existsByCrp(String crp);

    @Query("""
            select p from Psicologo p
            where
            p.ativo = true
            and
            p.especialidade = :especialidade
            and
            p.id not in(
                select c.psicologo.id from Sessao s
                where
                s.data = :data
            )
            order by random()
            limit 1
        """)
    Psicologo escolherPsicologoLivreNaData(Especialidade especialidade, LocalDateTime data);
    @Query("select p from Psicologo p where lower(p.nome) like %:nome%")
    List<Psicologo> findByNomeContainingIgnoreCase(@Param("nome") String nome);




}
