package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.repository.UserRepository;
import br.db.tec.e_commerce.service.user.UserService;
import jakarta.persistence.EntityExistsException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Deve registrar um usuário com sucesso e encriptar a senha")
    void shouldRegisterUserWithEncryptedPassword() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("test@email.com", "senha123", UserRole.CLIENTE);
        
        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        userService.register(dto);

        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userRepository, times(1)).save(any(Users.class));
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
