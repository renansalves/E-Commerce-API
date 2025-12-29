package br.db.tec.e_commerce.mapper.order;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.db.tec.e_commerce.domain.order.OrderItems;
import br.db.tec.e_commerce.domain.order.Orders;
import br.db.tec.e_commerce.dto.order.OrderItemResponseDTO;
import br.db.tec.e_commerce.dto.order.OrderResponseDTO;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  
  @Mapping(target = "status", source = "order.orderStatus")
  @Mapping(target = "items", source = "orderItems")
  OrderResponseDTO toResponseDTO(Orders order, List<OrderItems> orderItems);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "unitPriceSnapshot", source = "unitPrice")
  OrderItemResponseDTO tOrderItemResponseDTO(OrderItems item);

  @Mapping(target = "status", source = "orderStatus")
  @Mapping(target = "items", ignore = true) 
  OrderResponseDTO toSummaryDTO(Orders order);

  List<OrderResponseDTO> toResponseDTOList(List<Orders> orders);
}
