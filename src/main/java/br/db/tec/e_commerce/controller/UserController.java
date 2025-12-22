package br.db.tec.e_commerce.controller;

import br.db.tec.e_commerce.dto.auth.LoginRequestDTO;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoint responsavel por gerenciar authenticação dos usuarios.")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
    summary = "Registra novo usuário.",
    description = "Realiza o registro do usuario com as informações de nome e email."
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDTO dto) {
        userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(
    summary = "Logar",
    description = "Logar um usuario existente no banco, atravez da sua senha."
    )
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequestDTO dto) {
      String token = userService.authenticate(dto);
      return ResponseEntity.ok(Map.of("token", token));
    }

}
