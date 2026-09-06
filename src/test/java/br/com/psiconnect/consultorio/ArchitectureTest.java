package br.com.psiconnect.consultorio;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureTest {
    @Test
    void respeitaDirecaoDasDependencias() throws Exception {
        Path root = Path.of("src/main/java/br/com/psiconnect/consultorio");
        try (var paths = Files.walk(root)) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
                String source = Files.readString(path);
                if (path.startsWith(root.resolve("domain"))) {
                    assertThat(source).as("%s", path)
                            .doesNotContain("consultorio.application", "consultorio.infrastructure",
                                    "org.springframework", "DadosCadastro", "DadosAtualizacao");
                }
                if (path.startsWith(root.resolve("application"))) {
                    assertThat(source).as("%s", path)
                            .doesNotContain("consultorio.infrastructure", "org.springframework.data.jpa",
                                    "org.springframework.web", "ResponseEntity");
                }
                if (path.startsWith(root.resolve("infrastructure/web"))) {
                    assertThat(source).as("%s", path)
                            .doesNotContain("consultorio.infrastructure.persistence",
                                    "consultorio.application.port", "@Transactional");
                }
            }
        }
    }
}