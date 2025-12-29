package br.db.tec.e_commerce.E2E;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import br.db.tec.e_commerce.E2E.BaseE2ETest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderE2ETest extends BaseE2ETest{

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = "/api";
  }

  @Test
  @DisplayName("Fluxo Completo: Login, Adicionar ao Carrinho e Checkout")
  void shouldPerformFullPurchaseFlow() {

    Long productId = productRepository.findAll().get(0).getId();

    String token = loginAndGetToken("user@db.com", "123456789");

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body("{\"productId\": " + productId + ", \"quantity\": 2}")
        .log().all()
        .when()
        .post("/cart/items")
        .then()
        .log().all()
        .statusCode(201);

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .log().all()
        .post("/orders/checkout")
        .then()
        .log().all()
        .statusCode(201)
        .body("status", equalTo("PENDING"));
  }
}
