package br.db.tec.e_commerce.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.db.tec.e_commerce.dto.category.CategoryResponseDTO;
import br.db.tec.e_commerce.service.category.CategoryService;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve listar todas as categorias com paginação")
    @WithMockUser(roles = "CLIIENTE")
    void shouldReturnPagedCategories() throws Exception {
        when(categoryService.listAll(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve permitir que admin crie uma categoria")
    void shouldAllowAdminToCreateCategory() throws Exception {
        CategoryResponseDTO response = new CategoryResponseDTO(1L, "Eletrônicos", "Produtos eletronicos");
        when(categoryService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/categories/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Eletrônicos\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve retornar 204 ao deletar categoria")
    void shouldReturnNoContentOnDelete() throws Exception {
        mockMvc.perform(delete("/api/categories/admin/1"))
                .andExpect(status().isNoContent());
    }
}
