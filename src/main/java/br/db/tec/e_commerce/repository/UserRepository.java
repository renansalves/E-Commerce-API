package br.db.tec.e_commerce.repository;

import java.util.Optional;
import br.db.tec.e_commerce.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long>{

    Optional<Users> findByEmail(String string);
    boolean existsByEmail(String email);
  
}
