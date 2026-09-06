package br.com.psiconnect.consultorio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.jpa.open-in-view=false",
        "spring.datasource.url=jdbc:h2:mem:ddd-api;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
class PacienteApiTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @Test
    void mantemCadastroConsultaAtualizacaoEAltaSemTransacaoNoController() throws Exception {
        String cadastro = """
                {
                  "nome": "Ana Arquitetura",
                  "cpf": "12345678901",
                  "dataNascimento": "1990-01-01",
                  "profissao": "Professora",
                  "contato": {"telefone": "11999999999", "email": "ana@example.com"},
                  "endereco": {"logradouro": "Rua A", "bairro": "Centro", "cep": "12345678",
                               "numero": "1", "complemento": "", "cidade": "Cidade", "uf": "SP"}
                }
                """;
        var response = mvc.perform(post("/pacientes").contentType(MediaType.APPLICATION_JSON).content(cadastro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana Arquitetura"))
                .andReturn().getResponse();
        long id = mapper.readTree(response.getContentAsString()).get("id").asLong();
        mvc.perform(get("/pacientes/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/pacientes/nome/{nome}", "ANA ARQUITETURA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(put("/pacientes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"valorConsulta\":150.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorConsulta").value(150.00));
        mvc.perform(get("/pacientes/{id}", id))
                .andExpect(jsonPath("$.valorConsulta").value(150.00));
        mvc.perform(put("/pacientes/alta").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"motivoAlta\":\"Tratamento concluído\"}"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/pacientes/{id}", id))
                .andExpect(jsonPath("$.status").value(false));
    }
}