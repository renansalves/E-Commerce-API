package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.mapper.order.OrderMapper;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.OrderItemsRepository;
import br.db.tec.e_commerce.repository.OrdersRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.service.order.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private OrdersRepository ordersRepository;
    @Mock private CartItemsRepository cartItemsRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartsRepository cartsRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemsRepository orderItemsRepository;

    @InjectMocks private OrderService orderService;

    @Test
    @DisplayName("Deve finalizar checkout com sucesso e baixar stock")
    void checkoutSuccess() {
        // Arrange
        Long userId = 1L;
        Users user = new Users();
        Carts cart = new Carts();
        cart.setUser(user);

        Product p = new Product();
        p.setStockQuantity(10);
        p.setPriceCents(1000L);

        CartItems item = new CartItems();
        item.setProduct(p);
        item.setQuantity(2);
        item.setUnitPrice(1000L);

        when(cartsRepository.findByUser_Id(userId)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCarts(cart)).thenReturn(List.of(item));
        when(ordersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        orderService.checkout(userId);

        assertEquals(8, p.getStockQuantity()); // 10 - 2
        verify(orderItemsRepository, times(1)).saveAll(anyList());
        verify(cartItemsRepository, times(1)).deleteAll(anyList());
    }
}
