package br.db.tec.e_commerce.TestInfra;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbCleaner{
  private final JdbcTemplate jdbc;
  
  public DbCleaner (JdbcTemplate jdbcTemplate){
    this.jdbc = jdbcTemplate;
  }

  
 public void truncateAll() {
        jdbc.execute("""
            TRUNCATE TABLE ecommerce.order_items,
                           ecommerce.orders,
                           ecommerce.cart_items,
                           ecommerce.carts,
                           ecommerce.product,
                           ecommerce.category,
                           ecommerce.users
            RESTART IDENTITY CASCADE
        """);
    }

}
