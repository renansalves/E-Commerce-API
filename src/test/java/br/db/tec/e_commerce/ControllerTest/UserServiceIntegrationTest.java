
package br.db.tec.e_commerce.ControllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import br.db.tec.e_commerce.controller.UserController;
import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.dto.auth.LoginRequestDTO;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private UserRegisterRequestDTO registerRequestDto;
  private LoginRequestDTO loginRequestDto;

  @BeforeEach
  void setUp() {
    // Use dados fictícios válidos para não depender de nulls
    registerRequestDto = new UserRegisterRequestDTO("Renan", "renan@example.com", "senhaSegura123", UserRole.CLIENTE);
    loginRequestDto = new LoginRequestDTO("renan@example.com", "senhaSegura123");
  }

  @Test
  @DisplayName("Deve registra um usuário com sucesso.")
  void testRegisterUser() {
    doNothing().when(userService).register(any(UserRegisterRequestDTO.class));

    ResponseEntity<Void> response = userController.register(registerRequestDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNull(response.getBody());
    verify(userService).register(any(UserRegisterRequestDTO.class));
  }

  @DisplayName("Deve testar se o usuário está logado e possui o token jwt.")
  void testLoginUser() {
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    when(userService.authenticate(any(LoginRequestDTO.class))).thenReturn("token-jwt-mockado");

    ResponseEntity<Map<String, String>> response = userController.login(loginRequestDto, mockResponse);

    // Agora o controller retorna body JSON com o token -> não deve ser null
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody(), "O body deve conter o token");
    assertEquals("token-jwt-mockado", response.getBody().get("token"));

    // Verifica cookie 'jwt' setado no response
    Cookie[] cookies = mockResponse.getCookies();
    assertNotNull(cookies);
    assertTrue(cookies.length > 0, "Deve haver pelo menos um cookie setado");
    Cookie jwtCookie = null;
    for (Cookie c : cookies) {
      if ("jwt".equals(c.getName())) {
        jwtCookie = c;
        break;
      }
    }
    assertNotNull(jwtCookie, "Cookie 'jwt' deve estar presente");
    assertEquals("token-jwt-mockado", jwtCookie.getValue());
    assertTrue(jwtCookie.isHttpOnly());
    assertTrue(jwtCookie.getSecure());
    assertEquals("/", jwtCookie.getPath());
    assertEquals(60 * 60 * 2, jwtCookie.getMaxAge());
  }

  @Test
  @DisplayName("Deve realizar o logout de um usuario logado, removendo os cookies com token gerado.")
  void testLogoutUser() {
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    ResponseEntity<Void> response = userController.logout(mockResponse);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody(), "Para ResponseEntity<Void>, o body deve ser null");

    // Verifica se o cookie 'jwt' foi 'apagado' (valor vazio e MaxAge=0, por
    // exemplo)
    Cookie[] cookies = mockResponse.getCookies();
    assertNotNull(cookies);
    Cookie cleared = null;
    for (Cookie c : cookies) {
      if ("jwt".equals(c.getName())) {
        cleared = c;
        break;
      }
    }
    assertNotNull(cleared, "Cookie 'jwt' deve ser redefinido no logout");
    assertEquals("", cleared.getValue(), "Valor do cookie deve ser esvaziado");
    assertEquals(0, cleared.getMaxAge(), "MaxAge deve ser 0 para expirar imediatamente");
  }
}
