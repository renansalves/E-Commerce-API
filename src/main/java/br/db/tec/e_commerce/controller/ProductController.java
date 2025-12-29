package br.db.tec.e_commerce.controller;

import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.tec.e_commerce.dto.product.ProductRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductResponseDTO;
import br.db.tec.e_commerce.mapper.product.ProductMapper;
import br.db.tec.e_commerce.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProductController {

  @Autowired 
  ProductService productService;
  @Autowired
  ProductMapper productMapper;

  @GetMapping("/products")
  @PageableAsQueryParam
  @Operation(
  summary = "Lista todos os produtos",
  description = "Lista todos os produtos cadastrados, ativos e inativos."
  )
  public ResponseEntity<Page<ProductResponseDTO>> ListAllProducts(@Parameter(hidden = true) Pageable pageable) {
   Page<ProductResponseDTO> products = productService.listAll(pageable);
   return ResponseEntity.ok(products);
  }

  @GetMapping("/products/{id}")
  @Operation(
  summary = "Lista produtos",
  description = "Lista um produto pelo seu id."
  )
  public ResponseEntity<ProductResponseDTO> getProducts(@PathVariable Long id){
    ProductResponseDTO product = productService.findByIdAndActive(id);
    return ResponseEntity.status(HttpStatus.FOUND).body(product);
  }


  @PostMapping("/admin/products")
  @Operation(
  summary = "Criar Produto",
  description = "Adiciona um novo produto na tabela de produtos."
  )
  public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductRequestDTO dto){
   ProductResponseDTO savedProduct = productService.create(dto);
   return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
  }
  
  @PutMapping("/admin/products/{id}")
  @Operation(
  summary = "Modifica um produto",
  description = "Modifica um produto existente, atravez de um id valido."
  )
  public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO dto){

    ProductResponseDTO updatedProduct = productService.update(id, dto);
    return ResponseEntity.ok(updatedProduct);

  }

  @DeleteMapping("/admin/products/{id}")
  @Operation(
  summary = "Remove um produto",
  description = "Remove um produto existente e com id valido."
  )
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
    productService.deactivateProduct(id);
    return ResponseEntity.noContent().build();
  }
}
