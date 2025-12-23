package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import br.db.tec.e_commerce.mapper.category.CategoryMapper;
import br.db.tec.e_commerce.Builder.CategoryBuilder;
import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.dto.category.CategoryRequestDTO;
import br.db.tec.e_commerce.dto.category.CategoryResponseDTO;
import br.db.tec.e_commerce.exception.GlobalExceptionHandler;
import br.db.tec.e_commerce.repository.CategoryRepository;
import br.db.tec.e_commerce.service.category.CategoryService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @InjectMocks
  private CategoryService categoryService;

  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private CategoryMapper categoryMapper;

  private Category category;
  private CategoryRequestDTO categoryRequestDTO;
  private CategoryResponseDTO categoryResponseDTO;
  private CategoryBuilder categoryBuilder;

  @BeforeEach
  void setUp() {
    this.categoryBuilder = new CategoryBuilder()
      .withId(1L)
      .withName("Perifericos")
      .withDescription("Perifericos de informatica, teclado/mouse, monitores, etc...")
      .withCreatedDate(OffsetDateTime.now());
    this.category = categoryBuilder.buildCategory();
    this.categoryRequestDTO = categoryBuilder.buildCategoryRequestDTO();
    this.categoryResponseDTO = categoryBuilder.buildCategoryResponseDTO();

  }

  @Test
  @DisplayName("Deve adicionar uma categoria com sucesso")
  void shouldCreateNewCategory() {

    when(categoryMapper.toEntity(categoryRequestDTO)).thenReturn(this.category);
    when(categoryRepository.save(eq(category))).thenReturn(this.category);
    when(categoryMapper.toResponseDTO(category)).thenReturn(this.categoryBuilder.buildCategoryResponseDTO());

    CategoryResponseDTO result = categoryService.save(categoryRequestDTO);

    assertNotNull(result);
    verify(categoryMapper, times(1)).toEntity(this.categoryRequestDTO);
    verify(categoryRepository, times(1)).save(this.category);
    verify(categoryMapper, times(1)).toResponseDTO(this.category);

  }

  @Test
  @DisplayName("Deve listar uma categoria pelo seu id.")
  void ShouldListAnCategoryById() {
    Category category = categoryBuilder.buildCategory();
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(categoryMapper.toResponseDTO(category)).thenReturn(this.categoryResponseDTO);

    CategoryResponseDTO result = categoryService.findById(1L);

    assertNotNull(result);
    assertEquals("Perifericos", result.name());
    verify(categoryRepository, times(1)).findById(1L);
    verify(categoryMapper, times(1)).toResponseDTO(category);
  }

  @Test
  @DisplayName("Deve retornar uma exceção de categoruia não encontrada para o id informado")
  void ShouldReturnAnExceptionWhenCategoryIsNotFindById() {
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, 
        ()-> categoryService.findById(99L));

    verify(categoryRepository, times(1)).findById(99L);
  }
  @Test
  @DisplayName("Deve atualizar uma categoria existente.")
  void ShouldUpdateAnExistingCategory() {
      Category existCategory = CategoryBuilder
        .anCategory()
        .withId(1L)
        .withName("Categoria existente")
        .withDescription("Essa categoria existe")
        .buildCategory();

      CategoryRequestDTO requestDto = CategoryBuilder.anCategory()
          .withName("Categoria Atualizada")
          .withDescription("Atualiza uma categoria existente.")
          .buildCategoryRequestDTO();

      Category updatedCategory = CategoryBuilder.anCategory()
          .withName("Categoria Atualizada")
          .withDescription("Atualiza uma categoria existente.")
          .buildCategory();
      
      CategoryResponseDTO responseDto = CategoryBuilder.anCategory()
          .withName("Categoria Atualizada")
          .withDescription("Atualiza uma categoria existente.")
          .buildCategoryResponseDTO();

      when(categoryRepository.findById(1L)).thenReturn(Optional.of(existCategory));
      when(categoryRepository.save(eq(existCategory))).thenReturn(updatedCategory);
      when(categoryMapper.toResponseDTO(updatedCategory)).thenReturn(responseDto);

      CategoryResponseDTO result = categoryService.update(1L, requestDto);

      assertEquals("Categoria Atualizada", result.name());
      assertEquals("Atualiza uma categoria existente.", result.description());
  }
  @Test
  void ShouldDeleteAnCategoryById(){
    Long id = 1L;
    when(categoryRepository.existsById(id)).thenReturn(true);
    doNothing().when(categoryRepository).deleteById(id);

    assertDoesNotThrow(() -> categoryService.delete(id));

    verify(categoryRepository,times(1)).existsById(id);
    verify(categoryRepository,times(1)).existsById(id);
  }


}
