package br.db.tec.e_commerce.controller;

import org.hibernate.query.Page;
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
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api")
public class ProductController {


  @GetMapping("/products")
  @Operation(
  summary = "Lista todos os produtos",
  description = "Lista todos os produtos cadastrados, ativos e inativos."
  )
  public ResponseEntity<Page> ListAllProducts() {
  return null;
  }

  @GetMapping("/products/{id}")
  @Operation(
  summary = "Lista produtos",
  description = "Lista um produto pelo seu id."
  )
  public ResponseEntity<ProductResponseDTO> getProducts(@PathVariable Long id){
    return null;
  }


  @PostMapping("/admin/products")
  @Operation(
  summary = "Criar Produto",
  description = "Adiciona um novo produto na tabela de produtos."
  )
  public void createProduct(@RequestBody ProductRequestDTO dto){

  }
  
  @PutMapping("/admin/products/{id}")
  @Operation(
  summary = "Modifica um produto",
  description = "Modifica um produto existente, atravez de um id valido."
  )
  public void updateProduct(@PathVariable Long id){

  }

  @DeleteMapping("/admin/products/{id}")
  @Operation(
  summary = "Remove um produto",
  description = "Remove um produto existente e com id valido."
  )
  public void deleteProduct(@PathVariable Long id){

  }
}
