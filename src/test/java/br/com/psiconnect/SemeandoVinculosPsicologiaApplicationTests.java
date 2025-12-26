package br.com.psiconnect;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class SemeandoVinculosPsicologiaApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoadsWithApplicationBean() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.getBeansOfType(Application.class)).hasSize(1);
	}

	@Test
	void applicationStartsInNonWebMode() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(Application.class)
				.web(WebApplicationType.NONE)
				.run()) {
			assertThat(context.isActive()).isTrue();
			assertThat(context.getBean(Application.class)).isNotNull();
		}
	}
}
