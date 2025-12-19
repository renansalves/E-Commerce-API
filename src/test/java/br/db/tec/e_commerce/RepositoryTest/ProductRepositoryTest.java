package br.db.tec.e_commerce.RepositoryTest;

import br.db.tec.e_commerce.domain.Product;
import br.db.tec.e_commerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save and find product by ID")
    void shouldSaveAndFindProduct() {
        Product product = new Product();
        product.setSku("SKU-123");
        product.setName("Notebook Gamer");
        product.setPriceCents(500000L); // 5000.00
        product.setCurrency("BRL");
        product.setActive(true);
        product.setCreatedAt(OffsetDateTime.now());

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(productRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should throw exception when saving duplicate SKU")
    void shouldThrowExceptionOnDuplicateSku() {
        Product p1 = new Product();
        p1.setSku("UNIQUE-SKU");
        p1.setName("Prod 1");
        p1.setPriceCents(100L);
        p1.setActive(true);
        p1.setCreatedAt(OffsetDateTime.now());

        productRepository.save(p1);

        Product p2 = new Product();
        p2.setSku("UNIQUE-SKU"); // Mesmo SKU
        p2.setName("Prod 2");
        p2.setPriceCents(200L);
        p2.setActive(true);
        p2.setCreatedAt(OffsetDateTime.now());

        // O flush é necessário para forçar a escrita no banco e disparar a constraint
        assertThrows(DataIntegrityViolationException.class, () -> {
            productRepository.saveAndFlush(p2);
        });
    }
}
