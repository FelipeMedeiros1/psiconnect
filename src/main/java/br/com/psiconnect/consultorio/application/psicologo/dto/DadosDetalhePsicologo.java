package br.com.psiconnect.consultorio.application.psicologo.dto;

import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

public record DadosDetalhePsicologo(
        Long id, Boolean ativo, String nome, String crp, Contato contato, Especialidade especialidade) {

    public DadosDetalhePsicologo(Psicologo psicologo) {
        this(psicologo.getId(), psicologo.getAtivo(), psicologo.getNome(), psicologo.getCrp(), psicologo.getContato(), psicologo.getEspecialidade());
    }
}