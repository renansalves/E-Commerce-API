package br.db.tec.e_commerce.RepositoryTest;

import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.repository.ProductRepository;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertNotNull(saved.getId());
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

    @Test
    @DisplayName("Deve filtrar produtos dentro da faixa de preço correta")
    void shouldFindProductsWithinPriceRange() {
        Product p1 = new Product();
        p1.setSku("UNIQUE-SKU-1");
        p1.setName("Prod 1");
        p1.setPriceCents(150L);

        Product p2 = new Product();
        p2.setSku("UNIQUE-SKU-2");
        p2.setName("Prod 2");
        p2.setPriceCents(50L);
        
        Product p3 = new Product();
        p3.setSku("UNIQUE-SKU-3");
        p3.setName("Prod 3");
        p3.setPriceCents(5000L);

        Product p4 = new Product();
        p4.setSku("UNIQUE-SKU-4");
        p4.setName("Prod 4");
        p4.setPriceCents(2500L);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);
        
        Page<Product> result = productRepository.findByActiveTrueAndPriceCentsBetween(1000L, 6000L, Pageable.unpaged());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().get(0).getPriceCents() <= 6000L);
    }
}
