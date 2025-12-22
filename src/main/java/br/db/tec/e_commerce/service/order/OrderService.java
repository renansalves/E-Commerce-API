package br.db.tec.e_commerce.service.order;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.order.*;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.order.OrderResponseDTO;
import br.db.tec.e_commerce.mapper.order.OrderMapper;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.OrderItemsRepository;
import br.db.tec.e_commerce.repository.OrdersRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

  @Autowired private OrderItemsRepository orderItemsRepository;
  @Autowired private CartItemsRepository cartItemsRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private OrdersRepository ordersRepository;
  @Autowired private CartsRepository cartsRepository;
  @Autowired private OrderMapper orderMapper;

  @Transactional
  public OrderResponseDTO checkout() {

    Users currentUser = getAuthenticatedUser();
    
    Carts cart = cartsRepository.findByUser_Id(currentUser.getId())
        .orElseThrow(() -> new EntityNotFoundException("Carrinho não encontrado para este usuário"));
    List<CartItems> cartItems = cartItemsRepository.findByCarts(cart);

    if (cartItems.isEmpty()) {
      throw new RuntimeException("Não é possível finalizar um pedido com o carrinho vazio");
    } else {

      for (CartItems item : cartItems) {
        Product product = item.getProduct();
        if (product.getStockQuantity() < item.getQuantity()) {
          throw new RuntimeException("Estoque insuficiente para o produto: " + item.getProduct().getName());
        }
        product.setStockQuantity(product.getStockQuantity()-item.getQuantity());
        productRepository.save(product);
      }
    }

    Orders order = new Orders();
    order.setUser(cart.getUser());
    order.setOrderStatus(OrderStatus.PENDING);
    order.setCreatedAt(OffsetDateTime.now());

    long totalCents = cartItems.stream()
        .mapToLong(item -> (long) (item.getQuantity() * item.getUnitPrice()))
        .sum();
    order.setTotalCents(totalCents);

    final Orders savedOrder = ordersRepository.save(order);

    List<OrderItems> orderItems = cartItems.stream().map(cartItem -> {
      OrderItems orderItem = new OrderItems();
      orderItem.setOrders(savedOrder);
      orderItem.setProduct(cartItem.getProduct());
      orderItem.setQuantity(cartItem.getQuantity());
      // Aqui aplicamos o snapshot: o preço do pedido nunca muda, mesmo que o produto
      // mude
      orderItem.setUnitPrice(cartItem.getUnitPrice().longValue());
      return orderItem;
    }).collect(Collectors.toList());

    orderItemsRepository.saveAll(orderItems);

    cartItemsRepository.deleteAll(cartItems);

    return orderMapper.toResponseDTO(savedOrder, orderItems);
  }

  private Users getAuthenticatedUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return (Users) authentication.getPrincipal();
  }
}
