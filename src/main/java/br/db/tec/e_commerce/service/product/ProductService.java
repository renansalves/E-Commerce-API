package br.db.tec.e_commerce.service.product;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.dto.product.ProductRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductResponseDTO;
import br.db.tec.e_commerce.mapper.product.ProductMapper;
import br.db.tec.e_commerce.repository.CategoryRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductMapper productMapper;

  @Autowired 
  private CategoryRepository categoryRepository;

  public ProductResponseDTO create(ProductRequestDTO dto) {
    Category category = categoryRepository.findById(dto.categoryId()).
      orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

    Product product = productMapper.toEntity(dto);
    product.setCategory(category);
    return productMapper.toResponseDTO(productRepository.save(product));
  }

  public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    productMapper.updateEntityFromDto(dto, product);
    return productMapper.toResponseDTO(productRepository.save(product));
  }

  public Page<ProductResponseDTO> listAll(Pageable pageable) {
    return productRepository.findAll(pageable)
        .map(productMapper::toResponseDTO);
  }

  @Transactional
  public void decreaseStock(Long productId, Integer quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    if (product.getStockQuantity() < quantity) {
      throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
    }
    product.setStockQuantity(product.getStockQuantity() - quantity);
    product.setUpdatedAt(OffsetDateTime.now());
    productRepository.save(product);
  }

  @Transactional
  public void delete(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    product.setActive(false);
    product.setUpdatedAt(OffsetDateTime.now());
    productRepository.save(product);
  }

  public ProductResponseDTO search(Long id) {
    Product product = productRepository.findByIdAndActive(id, true)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    return productMapper.toResponseDTO(product);

  }

}
