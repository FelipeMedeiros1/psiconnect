package br.com.psiconnect.consultorio.psicologo;

import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import br.com.psiconnect.consultorio.psicologo.dto.DadosAtualizacaoPsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosCadastroPsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosDetalhePsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosListagemPsicologo;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PsicologoService {
    private final PsicologoRepository psicologoRepository;

    public PsicologoService(PsicologoRepository psicologoRepository) {
        this.psicologoRepository = psicologoRepository;
    }

    public DadosDetalhePsicologo cadastrar(DadosCadastroPsicologo dados) {
        var jaCadastrado = psicologoRepository.existsByCrp(dados.crp());

        if (jaCadastrado) {
            throw new ConsultorioException("Este CRP: " + dados.crp() + ", já está cadastrado");
        }
        Psicologo psicologo = new Psicologo(dados);
        return new DadosDetalhePsicologo(psicologoRepository.save(psicologo));
    }

    public Page<DadosListagemPsicologo> listar(Pageable paginacao) {
        return psicologoRepository.findAll(paginacao).map(DadosListagemPsicologo::new);
    }

    public List<DadosDetalhePsicologo> buscarPorNome(String nome) {
        return psicologoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(DadosDetalhePsicologo::new)
                .collect(Collectors.toList());
    }

    public DadosDetalhePsicologo buscarPorId(Long id) {
        return psicologoRepository.findById(id)
                .map(DadosDetalhePsicologo::new)
                .orElseThrow(() -> new ConsultorioException("Psicólogo com id: " + id + ", não encontrado"));
    }

    public void atualizar(Long id, DadosAtualizacaoPsicologo dados) {
        Psicologo psicologo = psicologoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Psicólogo com id: " + id + ", não encontrado"));
        psicologo.atualizarInformacoes(dados);
        psicologoRepository.save(psicologo);
    }

    public void deletar(Long id) {
        psicologoRepository.deleteById(id);
    }

    public void desativar(Long id) {
        Psicologo psicologo = psicologoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Psicólogo com id: " + id + ", não encontrado"));
        psicologo.desativar();
        psicologoRepository.save(psicologo);
    }
}
