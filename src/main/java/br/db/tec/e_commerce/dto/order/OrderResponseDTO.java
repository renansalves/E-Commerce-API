package br.db.tec.e_commerce.dto.order;

import java.time.OffsetDateTime;
import java.util.List;

import br.db.tec.e_commerce.domain.order.OrderStatus;
import br.db.tec.e_commerce.dto.order.OrderItemResponseDTO;

public record OrderResponseDTO(
    Long id,
    OrderStatus status,
    Long totalCents,
    OffsetDateTime createdAt,
    List<OrderItemResponseDTO> items
) {}
