package br.com.psiconnect.consultorio.psicologo.dto;


import br.com.psiconnect.consultorio.psicologo.Especialidade;
import br.com.psiconnect.consultorio.psicologo.Psicologo;

public record DadosListagemPsicologo(
        Long id,
        Boolean ativo,
        String nome,
        String email,
        String telefone,
        String crp,
        Especialidade especialidade
) {

    public DadosListagemPsicologo(Psicologo psicologo) {
        this(
                psicologo.getId(),
                psicologo.getAtivo(),
                psicologo.getNome(),
                psicologo.getContato().getEmail(),
                psicologo.getContato().getTelefone(),
                psicologo.getCrp(),
                psicologo.getEspecialidade()
        );
    }

}
