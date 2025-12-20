
package br.db.tec.e_commerce.RepositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.repository.UserRepository;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  void shouldCreateUserClientAndReturn() {
    Users newUserClient = new Users(
        null,
        "usuario@test.com",
        "asbasdasd-asdqwdq",
        UserRole.CLIENTE,
        true,
        OffsetDateTime.now());

    entityManager.persist(newUserClient);
    entityManager.flush(); 

    Long id = newUserClient.getId();
    assertTrue(id != null && id > 0);

    Optional<Users> foundUser = userRepository.findById(id);
    assertTrue(foundUser.isPresent());
    assertEquals(newUserClient.getEmail(), foundUser.get().getEmail());
  }

  @Test
  @DisplayName("Deve persistir e encontrar utilizador por email")
  void shouldSaveAndFindUser() {
    Users user = new Users(
        null,
        "cliente@teste.com",
        "hashSeguro123",
        UserRole.CLIENTE,
        true,
        OffsetDateTime.now());

    userRepository.save(user);

    Optional<Users> foundUser = userRepository.findByEmail("cliente@teste.com");

    assertEquals(foundUser.get().getEmail(),user.getEmail() );
    assertEquals(foundUser.get().getRole(),UserRole.CLIENTE);
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar salvar email duplicado")
  void shouldThrowExceptionOnDuplicateEmail() {
    Users user1 = new Users(
        null,
        "duplicado@teste.com",
        "pass1",
        UserRole.ADMIN,
        true,
        OffsetDateTime.now());
    userRepository.save(user1);

    Users user2 = new Users(
        null,
        "duplicado@teste.com",
        "pass2",
        UserRole.CLIENTE,
        true,
        OffsetDateTime.now());

    assertThrows(DataIntegrityViolationException.class, () -> {
      userRepository.saveAndFlush(user2);
    });
  }
}
