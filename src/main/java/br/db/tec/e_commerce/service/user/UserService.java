package br.db.tec.e_commerce.service.user;

import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.user.UserRegisterRequestDTO;
import br.db.tec.e_commerce.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Requer Spring Security
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EntityExistsException("Este e-mail já está cadastrado.");
        }

        Users user = new Users();
        user.setEmail(dto.email());
        user.setRole(dto.role());
        
        // Criptografando a senha antes de salvar
        user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);
    }
    
    public Users findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
