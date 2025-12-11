package br.db.tec.e_commerce;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class ECommerceApplicationTests {

	@Test
	void contextLoads() {
	}

 @Test
    void mainShouldStartAndCloseContext() {
        assertDoesNotThrow(() -> {
            ConfigurableApplicationContext ctx = 
                org.springframework.boot.SpringApplication.run(ECommerceApplication.class, new String[] {});
            try {
                Object bean = ctx.getBean(ECommerceApplication.class);
                org.junit.jupiter.api.Assertions.assertNotNull(bean);
            } finally {
                ctx.close(); // sempre fechar para não deixar threads abertas
            }
        });
    }

}
