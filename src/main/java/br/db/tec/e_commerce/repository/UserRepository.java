package br.db.tec.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.Users;

public interface UserRepository extends JpaRepository<Users, Long>{

    Optional<Users> findByEmail(String string);
  
}
