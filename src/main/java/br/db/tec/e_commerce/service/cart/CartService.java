package br.db.tec.e_commerce.service.cart;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.cart.CartItemRequestDTO;
import br.db.tec.e_commerce.dto.cart.CartResponseDTO;
import br.db.tec.e_commerce.mapper.cart.CartMapper;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CartService {

  @Autowired private CartsRepository cartsRepository;
  @Autowired private CartItemsRepository cartItemsRepository;
  @Autowired private CartMapper cartMapper;
  @Autowired private ProductRepository productRepository;
  @Autowired private UserRepository userRepository;


    @Transactional
    public CartResponseDTO addItemToCart(Long userId, CartItemRequestDTO dto) {
        // Busca o carrinho do usuário. Se não existir, cria um novo.
        Carts cart = cartsRepository.findByUser_Id(userId)
            .orElseGet(() -> {
                Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
                
                Carts newCart = new Carts();
                newCart.setUser(user); // Aqui usamos o objeto user que buscamos
                newCart.setCreatedAt(OffsetDateTime.now());
                return cartsRepository.save(newCart);
            });

        // Lógica de adição de produto (já corrigida por você)
        Product product = productRepository.findById(dto.productId())
            .filter(Product::getActive)
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado ou inativo"));

        CartItems item = cartItemsRepository.findByCartsAndProduct(cart, product)
            .orElse(new CartItems());

        if (item.getId() == null) {
            item.setCarts(cart);
            item.setProduct(product);
            item.setQuantity(dto.quantity());
            item.setCreatedAt(OffsetDateTime.now());
        } else {
            item.setQuantity(item.getQuantity() + dto.quantity());
        }

        item.setUnitPrice(product.getPriceCents());
        cartItemsRepository.save(item);

        List<CartItems> allItems = cartItemsRepository.findByCarts(cart);
        return cartMapper.toResponseDTO(cart, allItems);
    }
  public CartResponseDTO getCartDetailed(Long cartId) {
    Carts cart = cartsRepository.findById(cartId)
        .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado"));

    List<CartItems> items = cartItemsRepository.findByCarts(cart);

    // O MapStruct gera a implementação que chama os métodos de cálculo automáticos
    return cartMapper.toResponseDTO(cart, items);
  }
}
