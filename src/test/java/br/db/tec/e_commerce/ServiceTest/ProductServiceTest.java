package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import br.db.tec.e_commerce.Builder.CategoryBuilder;
import br.db.tec.e_commerce.Builder.ProductBuilder;
import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.dto.product.ProductRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductResponseDTO;
import br.db.tec.e_commerce.mapper.product.ProductMapper;
import br.db.tec.e_commerce.repository.CategoryRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequestDTO productRequestDTO;
    private Product product;
    private ProductResponseDTO productResponseDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = CategoryBuilder.anCategory().buildCategory();
        productRequestDTO = ProductBuilder.anProduct().withCategoryId(category.getId()).buildProductRequestDTO();
        product = ProductBuilder.anProduct().withCategory(category).buildProduct();
        productResponseDTO = ProductBuilder.anProduct().withCategoryId(category.getId()).buildProductResponseDTO();
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProductSuccessfully() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productMapper.toEntity(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.create(productRequestDTO);

        assertNotNull(result);
        assertEquals(productResponseDTO, result);
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(productMapper, times(1)).toEntity(productRequestDTO);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toResponseDTO(product);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when category not found during product creation")
    void createProductThrowsEntityNotFoundWhenCategoryNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                productService.create(productRequestDTO));

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(categoryRepository, times(1)).findById(anyLong());
        verifyNoInteractions(productMapper, productRepository);
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProductSuccessfully() {
        Long productId = product.getId();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateEntityFromDto(productRequestDTO, product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.update(productId, productRequestDTO);

        assertNotNull(result);
        assertEquals(productResponseDTO, result);
        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).updateEntityFromDto(productRequestDTO, product);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toResponseDTO(product);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product not found during product update")
    void updateProductThrowsEntityNotFoundWhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                productService.update(productId, productRequestDTO));

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("Should list all products with pagination")
    void listAllProductsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Collections.singletonList(product);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(productResponseDTO);

        Page<ProductResponseDTO> result = productService.listAll(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(productResponseDTO, result.getContent().get(0));
        verify(productRepository, times(1)).findAll(pageable);
        verify(productMapper, times(1)).toResponseDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should decrease product stock successfully")
    void decreaseProductStockSuccessfully() {
        Long productId = product.getId();
        int quantityToDecrease = 5;
        product.setStockQuantity(10); 

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.decreaseStock(productId, quantityToDecrease);

        assertEquals(5, product.getStockQuantity()); 
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product not found during stock decrease")
    void decreaseProductStockThrowsEntityNotFoundWhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                productService.decreaseStock(productId, 5));

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when stock is insufficient")
    void decreaseProductStockThrowsInsufficientStockException() {
        Long productId = product.getId();
        int quantityToDecrease = 15;
        product.setStockQuantity(10); 

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () ->
                productService.decreaseStock(productId, quantityToDecrease));

        assertTrue(exception.getMessage().contains("Estoque insuficiente para o produto: "));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should deactivate product successfully")
    void deactivateProductSuccessfully() {
        Long productId = product.getId();
        product.setActive(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deactivateProduct(productId);

        assertFalse(product.getActive());
        assertNotNull(product.getUpdatedAt());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product not found during deactivation")
    void deactivateProductThrowsEntityNotFoundWhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                productService.deactivateProduct(productId));

        assertEquals("Produto não encontrado.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should find product by ID and active status successfully")
    void findByIdAndActiveSuccessfully() {
        Long productId = product.getId();
        product.setActive(true);

        when(productRepository.findByIdAndActive(productId, true)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.findByIdAndActive(productId);

        assertNotNull(result);
        assertEquals(productResponseDTO, result);
        verify(productRepository, times(1)).findByIdAndActive(productId, true);
        verify(productMapper, times(1)).toResponseDTO(product);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product not found by ID and active status")
    void findByIdAndActiveThrowsEntityNotFoundWhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.findByIdAndActive(productId, true)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                productService.findByIdAndActive(productId));

        assertEquals("Produto não encontrado.", exception.getMessage());
        verify(productRepository, times(1)).findByIdAndActive(productId, true);
        verifyNoInteractions(productMapper);
    }

}
