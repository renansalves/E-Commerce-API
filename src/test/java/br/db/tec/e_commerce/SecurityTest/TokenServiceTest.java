package br.db.tec.e_commerce.SecurityTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.security.TokenService;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-123");
    }

    @Test
    @DisplayName("Deve validar um token gerado corretamente")
    void shouldGenerateAndValidateToken() {
        Users user = new Users();
        user.setEmail("test@dbserver.com");
        user.setRole(UserRole.CLIENTE);

        String token = tokenService.generateToken(user);
        String subject = tokenService.validateToken(token);

        assertEquals("test@dbserver.com", subject);
    }

    @Test
    @DisplayName("Um erro deve ser lançado na tentativa de gerar o token com usuario nulo.")
    void shoultThrowAnExceptionWhenTryToGenerateANewTokenWithNullUser() {
        Users user = new Users();
        assertThrows(RuntimeException.class, (() -> tokenService.generateToken(user)));
    }

    @Test
    @DisplayName("Deve retornar null ao validar um token inválido ou malformado")
    void shouldReturnNullForInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.payload";
        
        String subject = tokenService.validateToken(invalidToken);
        
        assertNull(subject);
    }
}
