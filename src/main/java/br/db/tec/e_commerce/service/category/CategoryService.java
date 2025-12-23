package br.db.tec.e_commerce.service.category;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.dto.category.CategoryRequestDTO;
import br.db.tec.e_commerce.dto.category.CategoryResponseDTO;
import br.db.tec.e_commerce.mapper.category.CategoryMapper;
import br.db.tec.e_commerce.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CategoryMapper mapper;

  public Page<CategoryResponseDTO> listAll(Pageable pageable) {
    return categoryRepository.findAll(pageable)
        .map(mapper::toResponseDTO);
  }

  public CategoryResponseDTO findById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));
    return mapper.toResponseDTO(category);
  }

  @Transactional
  public CategoryResponseDTO save(CategoryRequestDTO dto) {
    if (categoryRepository.existsByName(dto.name())) {
      throw new IllegalArgumentException("Já existe uma categoria com este nome.");
    }

    Category category = mapper.toEntity(dto);
    return mapper.toResponseDTO(categoryRepository.save(category));
  }

  @Transactional
  public void delete(Long id) {
    if (!categoryRepository.existsById(id)) {
      throw new EntityNotFoundException("Impossível excluir: Categoria não encontrada.");
    }
    categoryRepository.deleteById(id);
  }

  @Transactional
  public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
    Optional<Category> category = categoryRepository.findById(id);
    if (category == null) {
      throw new EntityNotFoundException("Categoria com id:" + id + " não encontrada.");
    }
      
      category.get().setName(dto.name());
      category.get().setDescription(dto.description());
      categoryRepository.save(category.get());
      CategoryResponseDTO updatedCategory = mapper.toResponseDTO(category.get());

    return updatedCategory;

  }
}
