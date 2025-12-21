package br.db.tec.e_commerce.UserControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.repository.UserRepository;
import br.db.tec.e_commerce.service.user.UserService;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve persistir usuário no banco com senha criptografada")
    void shouldPersistUserIntegration() {
        // Arrange
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO(
            "integracao@db.com", 
            "senhaForte123", 
            UserRole.CLIENTE
        );

        // Act
        userService.register(dto);

        // Assert
        var savedUser = userRepository.findByEmail("integracao@db.com").orElseThrow();
        assertNotNull(savedUser.getId());
        assertNotEquals("senhaForte123", savedUser.getPassword());
    }
}
