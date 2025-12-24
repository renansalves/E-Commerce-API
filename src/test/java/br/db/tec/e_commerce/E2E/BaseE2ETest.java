package br.db.tec.e_commerce.e2e;

import static io.restassured.RestAssured.given;

import java.time.OffsetDateTime;
import java.time.OffsetTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.repository.CategoryRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseE2ETest {

  @LocalServerPort
  protected int port;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected PasswordEncoder passwordEncoder;
  @Autowired
  protected CategoryRepository categoryRepository;
  @Autowired
  protected ProductRepository productRepository;

  @BeforeEach
  void setUpBase() {
    RestAssured.port = port;
    RestAssured.basePath = "/api";

    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    setupTestData();
  }

  protected void setupTestData() {
    createTestUser("admin@db.com", "123456789", UserRole.ADMIN);
    createTestUser("user@db.com",  "123456789", UserRole.CLIENTE);

    Category cat = categoryRepository.save(new Category(null, "Eletrónicos","Eletronicos e informatica", OffsetDateTime.now()));
    Product prod = new Product();
    prod.setName("Teclado");
    prod.setSku("SKU-Teclado-123");
    prod.setPriceCents(10000L);
    prod.setStockQuantity(50);
    prod.setCategory(cat);
    prod.setActive(true);
    productRepository.save(prod);
  }

  protected void createTestUser(String email, String password, UserRole role) {
    Users user = new Users();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(role);
    userRepository.save(user);
  }

  protected String loginAndGetToken(String email, String password) {
    return given()
        .contentType(ContentType.JSON)
        .body(new LoginRequest(email, password))
        .log().all()
        .when()
        .post("/users/login")
        .then()
        .log().all()
        .statusCode(200)
        .extract()
        .path("token");
  }

  private record LoginRequest(String email, String password) {
  }
}
