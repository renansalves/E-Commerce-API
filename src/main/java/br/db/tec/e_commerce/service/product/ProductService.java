package br.db.tec.e_commerce.service.product;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.dto.product.ProductRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductResponseDTO;
import br.db.tec.e_commerce.mapper.product.ProductMapper;
import br.db.tec.e_commerce.repository.CategoryRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.exception.InsufficientStockException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private CategoryRepository categoryRepository;

  public ProductResponseDTO create(ProductRequestDTO dto) {
    Category category = categoryRepository.findById(dto.categoryId())
        .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

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

  public Page<ProductResponseDTO> listAll(
      Long categoryId,
      Long minPriceCents,
      Long maxPriceCents,
      Boolean active,
      Boolean inStock,
      String query,
      Pageable pageable) {
    Specification<Product> spec = Specification.where(null);

    if (categoryId != null) {
      spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("id"), categoryId));
    }

    if (minPriceCents != null && minPriceCents >= 0) {
      spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("priceCents"), minPriceCents));
    }

    if (maxPriceCents != null && maxPriceCents >= 0) {
      spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("priceCents"), maxPriceCents)); // <= CORRETO
    }

    if (active != null) {
      spec = spec.and((root, q, cb) -> cb.equal(root.get("active"), active));
    }

    if (inStock != null && inStock) {
      spec = spec.and((root, q, cb) -> cb.greaterThan(root.get("stockQuantity"), 0));
    }

    if (query != null && !query.isBlank()) {
      String like = "%" + query.trim().toLowerCase() + "%";
      spec = spec.and((root, q, cb) -> cb.or(
          cb.like(cb.lower(root.get("name")), like),
          cb.like(cb.lower(root.get("description")), like)));
    }

    return productRepository.findAll(spec, pageable)
        .map(productMapper::toResponseDTO);
  }

  @Transactional
  public void decreaseStock(Long productId, Integer quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    if (product.getStockQuantity() < quantity) {
      throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
    }
    product.setStockQuantity(product.getStockQuantity() - quantity);
    product.setUpdatedAt(OffsetDateTime.now());
    productRepository.save(product);
  }

  @Transactional
  public void deactivateProduct(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    product.setActive(false);
    product.setUpdatedAt(OffsetDateTime.now());
    productRepository.save(product);
  }

  public ProductResponseDTO findByIdAndActive(Long id) {
    Product product = productRepository.findByIdAndActive(id, true)
        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
    return productMapper.toResponseDTO(product);

  }

}
