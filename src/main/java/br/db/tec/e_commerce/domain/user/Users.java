package br.db.tec.e_commerce.domain.user;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", schema = "ecommerce")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank
  @Column(name = "password_hash", nullable = false, unique = true)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = ("ecomerce.user_role_enum"))
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private UserRole role;

  @Column(nullable = false)
  private Boolean enabled = true;

  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
  }

 @Override
  public String getPassword() {
      return this.password;
  }

  @Override
  public String getUsername() {
      return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
      return true;
  }

  @Override
  public boolean isAccountNonLocked() {
      return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
      return true;
  }

  @Override
  public boolean isEnabled() {
      return this.enabled;
  } 


}
