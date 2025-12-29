package br.db.tec.e_commerce.service.user;


import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.auth.LoginRequestDTO;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.repository.UserRepository;
import br.db.tec.e_commerce.security.TokenService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService; 

    @Transactional
    public void register(UserRegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EntityExistsException("Este e-mail já está cadastrado.");
        }

        Users user = new Users();
        user.setEmail(dto.email());
        user.setRole(dto.role());
        
        user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);
    }
    
    public Users findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public String authenticate(LoginRequestDTO dto){

      Users user = userRepository.findByEmail(dto.email())
        .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos"));
      if (!passwordEncoder.matches(dto.password(), user.getPassword())){
        throw new RuntimeException("E-mail ou senha inválida.");
      }

      return tokenService.generateToken(user);

    }
}
