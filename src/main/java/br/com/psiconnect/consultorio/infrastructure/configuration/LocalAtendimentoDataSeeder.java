package br.com.psiconnect.consultorio.infrastructure.configuration;

import br.com.psiconnect.consultorio.application.port.LocalAtendimentoRepository;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.localatendimento.LocalAtendimento;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class LocalAtendimentoDataSeeder implements ApplicationRunner {
    private final LocalAtendimentoRepository locais;

    public LocalAtendimentoDataSeeder(LocalAtendimentoRepository locais) {
        this.locais = locais;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        criarSeNaoExistir("Consultório - SANTANA", enderecoSantana());
        criarSeNaoExistir("ONLINE", null);
        criarSeNaoExistir("CASA - PACIENTE", null);
    }

    private void criarSeNaoExistir(String nome, Endereco endereco) {
        if (!locais.existsByNomeLugarIgnoreCase(nome)) {
            locais.save(new LocalAtendimento(nome, endereco));
        }
    }

    private Endereco enderecoSantana() {
        return new Endereco(
                "Rua Judith Zumkeller",
                "Parque Mandaqui",
                "02422020",
                "10",
                "",
                "São Paulo",
                "SP");
    }
}
