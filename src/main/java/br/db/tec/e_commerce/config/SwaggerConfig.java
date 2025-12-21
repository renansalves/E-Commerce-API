package br.db.tec.e_commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

  @Bean
  OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(new Info().title("E-COMMERCE API")
            .version("1.0")
            .description("API para E-COMMERCE digital."));

  }

}
