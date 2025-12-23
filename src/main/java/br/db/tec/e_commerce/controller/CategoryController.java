package br.db.tec.e_commerce.controller;

import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import br.db.tec.e_commerce.dto.category.CategoryRequestDTO;
import br.db.tec.e_commerce.dto.category.CategoryResponseDTO;
import br.db.tec.e_commerce.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

  @GetMapping
  @PageableAsQueryParam
  @Operation(
  summary = "Lista todas categorias",
  description = "Lista todos as categorias cadastrados"
  )
    public ResponseEntity<Page<CategoryResponseDTO>> getAll(@Parameter(hidden=true) Pageable pageable) {
      Page<CategoryResponseDTO> category = categoryService.listAll(pageable);
        return ResponseEntity.ok(category);
    }

  @Operation(
  summary = "Lista todas categorias",
  description = "Lista todos as categorias cadastrados"
  )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

  @Operation(
  summary = "Lista todas categorias",
  description = "Lista todos as categorias cadastrados"
  )
    @PostMapping("/admin")
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody @Valid CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

  @Operation(
  summary = "Lista todas categorias",
  description = "Lista todos as categorias cadastrados"
  )
    @PutMapping("/admin/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

  @Operation(
  summary = "Lista todas categorias",
  description = "Lista todos as categorias cadastrados"
  )
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
