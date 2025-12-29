package br.db.tec.e_commerce.Builder;

import br.db.tec.e_commerce.domain.order.OrderItems;
import br.db.tec.e_commerce.domain.order.OrderStatus;
import br.db.tec.e_commerce.domain.order.Orders;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.order.OrderItemResponseDTO;
import br.db.tec.e_commerce.dto.order.OrderResponseDTO;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {

    private Long id = 1L;
    private Users user;
    private OrderStatus status = OrderStatus.PENDING;
    private Long totalCents = 10000L;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private List<OrderItems> items = new ArrayList<>();

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public OrderBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder withUser(Users user) {
        this.user = user;
        return this;
    }

    public OrderBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withTotalCents(Long totalCents) {
        this.totalCents = totalCents;
        return this;
    }

    public OrderBuilder withItem(Product product, int quantity, Long unitPrice) {
        OrderItems item = new OrderItems();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        this.items.add(item);
        return this;
    }

    public Orders build() {
        Orders order = new Orders();
        order.setId(this.id);
        order.setUser(this.user);
        order.setOrderStatus(this.status);
        order.setTotalCents(this.totalCents);
        order.setCreatedAt(this.createdAt);
        this.items.forEach(item -> item.setOrders(order));
        return order;
    }

    public OrderResponseDTO buildResponseDTO() {
        List<OrderItemResponseDTO> itemDTOs = items.stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                )).toList();

        return new OrderResponseDTO(
                this.id,
                this.status,
                this.totalCents,
                this.createdAt,
                itemDTOs
        );
    }
}
