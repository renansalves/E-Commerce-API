package br.db.tec.e_commerce.mapper.category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.dto.category.CategoryRequestDTO;
import br.db.tec.e_commerce.dto.category.CategoryResponseDTO;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    Category toEntity(CategoryRequestDTO dto);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    CategoryResponseDTO toResponseDTO(Category category);
}
