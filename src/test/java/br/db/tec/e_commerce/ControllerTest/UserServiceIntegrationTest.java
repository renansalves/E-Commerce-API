package br.db.tec.e_commerce.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.db.tec.e_commerce.TestInfra.DbCleaner;
import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.repository.UserRepository;
import br.db.tec.e_commerce.security.TokenService;
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

  @MockitoBean
  private TokenService tokenService;

  @Autowired
  private DbCleaner dbCleaner;

  @BeforeEach
  void cleanDatabase() {
    dbCleaner.truncateAll();
  }

  @Test
  @DisplayName("Deve persistir usuário no banco com senha criptografada")
  void shouldPersistUserIntegration() {
    UserRegisterRequestDTO dto = new UserRegisterRequestDTO(
        "integracao@db.com",
        "senhaForte123",
        UserRole.CLIENTE);

    userService.register(dto);

    Users savedUser = userRepository.findByEmail("integracao@db.com").orElseThrow();
    assertNotNull(savedUser.getId());
    assertNotEquals("senhaForte123", savedUser.getPassword());
  }
}
