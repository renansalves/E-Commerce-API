package br.db.tec.e_commerce.UserServiceTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.repository.UserRepository;
import br.db.tec.e_commerce.service.user.UserService;
import jakarta.persistence.EntityExistsException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Deve registrar um usuário com sucesso e encriptar a senha")
    void registerUserSuccess() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("teste@db.com", "senha123", UserRole.CLIENTE);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        userService.register(dto);

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(userCaptor.capture());
        
        Users savedUser = userCaptor.getValue();
        assertNotEquals("senha123", savedUser.getPassword()); // Senha deve estar hashada
        assertTrue(savedUser.getPassword().length() > 20); 
    }

    @Test
    @DisplayName("Deve lançar exceção se o e-mail já existir")
    void registerUserEmailExists() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("existente@db.com", "12345678", UserRole.CLIENTE);
        when(userRepository.existsByEmail("existente@db.com")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }
}
