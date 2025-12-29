package br.db.tec.e_commerce.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.db.tec.e_commerce.Builder.ProductBuilder;
import br.db.tec.e_commerce.TestInfra.DbCleaner;
import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.dto.category.CategoryRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductResponseDTO;
import br.db.tec.e_commerce.repository.CategoryRepository;
import br.db.tec.e_commerce.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductController {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryRepository categoryRepository;

  private Product product;
  private ProductBuilder productBuilder;
  private ProductRequestDTO productRequestDTO;
  private ProductResponseDTO productResponseDTO;
  private Category category;

  @Autowired
  private DbCleaner dbCleaner;

  @BeforeEach
  void cleanDatabase() {
    dbCleaner.truncateAll();
  }

  @BeforeEach
  void setup() {
    category = new Category();
    category.setName("Default Category");
    category.setCreatedAt(OffsetDateTime.now());
    category = categoryRepository.save(category);

    this.productBuilder = ProductBuilder.anProduct()
        .withCategoryId(category.getId())
        .withCategory(category);

    this.product = this.productBuilder.buildProduct();
    this.product = productRepository.save(this.product);
    this.productRequestDTO = this.productBuilder.buildProductRequestDTO();
    this.productResponseDTO = this.productBuilder.buildProductResponseDTO();
  }

  @Test
  @DisplayName("Admin deve conseguir criar um produto")
  @WithMockUser(roles = "ADMIN")
  void shouldCreateProductWhenUserIsAdmin() throws Exception {

    ProductRequestDTO request = new ProductBuilder()
        .withSku("SKU-NEW-123")
        .withName("Novo Produto")
        .withCategoryId(category.getId())
        .withPriceCents(1000L)
        .withStockQuantity(10)
        .buildProductRequestDTO();

    mockMvc.perform(post("/api/admin/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.sku").value("SKU-NEW-123"));
  }

  @Test
  @DisplayName("Usuário comum não deve acessar rotas de admin")
  @WithMockUser(roles = "USER")
  void shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
    mockMvc.perform(delete("/api/admin/products/1"))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Delete deve apenas desativar o produto (Soft Delete)")
  @WithMockUser(roles = "ADMIN")
  void shouldDeactivateProductInsteadOfDeleting() throws Exception {
    Product product = new Product();
    product.setName("Mouse");
    product.setSku("00000000000000001");
    product.setPriceCents(5000L);
    product.setActive(true);
    var saved = productRepository.save(product);

    mockMvc.perform(delete("/api/admin/products/" + saved.getId()))
        .andExpect(status().isNoContent());

    Product result = productRepository.findById(saved.getId()).get();
    assertFalse(result.getActive());
  }

  @Test
  @DisplayName("Deve retornar 400 ao tentar criar produto com preço ou estoque negativo")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn400WhenPriceOrStockIsNegative() throws Exception {
    ProductRequestDTO invalidRequest = new ProductBuilder()
        .withSku("SKU-INV")
        .withName("Produto Inválido")
        .withPriceCents(-100L)
        .withActive(true)
        .withStockQuantity(-5)
        .buildProductRequestDTO();

    mockMvc.perform(post("/api/admin/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());

  }

  @Test
  @DisplayName("Deve retornar estrutura de paginação correta na listagem pública")
  void shouldReturnPagedProducts() throws Exception {
    mockMvc.perform(get("/api/products")
        .param("page", "0")
        .param("size", "5")
        .param("sort", "name,asc")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.number").value(0));
  }

}
