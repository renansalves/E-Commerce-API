package br.db.tec.e_commerce.service.cart;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @Autowired
  private CartsRepository cartsRepository;
  @Autowired
  private CartItemsRepository cartItemsRepository;
  @Autowired
  private CartMapper cartMapper;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private UserRepository userRepository;

  @Transactional
  public CartResponseDTO addItemToCart(CartItemRequestDTO dto) {
    Users currentUser = getAuthenticatedUser();
    Long userId = currentUser.getId();

    Carts cart = cartsRepository.findByUser_Id(userId)
        .orElseGet(() -> {
          Carts newCart = new Carts();
          newCart.setUser(currentUser);
          newCart.setCreatedAt(OffsetDateTime.now());
          return cartsRepository.save(newCart);
        });

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
    if (!product.getActive()) {
        throw new EntityNotFoundException("Produto inativo");
    }

    if (product.getStockQuantity() < dto.quantity()) {
        throw new IllegalArgumentException("Estoque insuficiente");
    }

    item.setUnitPrice(product.getPriceCents());
    cartItemsRepository.save(item);

    List<CartItems> allItems = cartItemsRepository.findByCarts(cart);
    return cartMapper.toResponseDTO(cart, allItems);
  }

  private Users getAuthenticatedUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return (Users) authentication.getPrincipal();
  }

  public CartResponseDTO getCartDetailed(Long cartId) {
    Carts cart = cartsRepository.findById(cartId)
        .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado"));

    List<CartItems> items = cartItemsRepository.findByCarts(cart);

    return cartMapper.toResponseDTO(cart, items);
  }

  public CartResponseDTO getCurrentCart() {
    Users user = getAuthenticatedUser();
    Carts cart = cartsRepository.findByUser_Id(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("Carrinho vazio ou não encontrado"));
    List <CartItems> items = cartItemsRepository.findByCarts(cart);
    return cartMapper.toResponseDTO(cart,items); // Usa o teu mapper para converter
  }

  @Transactional
  public void clearCart(){
    Users user = getAuthenticatedUser();

    Carts cart = cartsRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado"));
    cartItemsRepository.deleteByCarts(cart);

  }

  @Transactional
  public void removeItemFromCart(Long productId) {
    Users user = getAuthenticatedUser();
    cartItemsRepository.deleteByCarts_User_IdAndProduct_Id(user.getId(), productId);
  }
}
