package br.db.tec.e_commerce.mapper.cart;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.dto.cart.CartItemResponseDTO;
import br.db.tec.e_commerce.dto.cart.CartResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalCents", source = "items", qualifiedByName = "calculateTotal")
    CartResponseDTO toResponseDTO(Carts cart, List<CartItems> items);

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "unitPrice") // unit_price_snapshot do banco
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(item))")
    CartItemResponseDTO toItemResponseDTO(CartItems item);

    default Long calculateSubtotal(CartItems item) {
        if (item == null || item.getUnitPrice() == null)
          return 0L;
        long price = item.getUnitPrice().longValue(); 
        return item.getQuantity() * price;
    }

    // Lógica para o Total do Carrinho: Soma dos subtotais
    @Named("calculateTotal")
    default Long calculateTotal(List<CartItems> items) {
        if (items == null) return 0L;
        return items.stream()
                .mapToLong(this::calculateSubtotal)
                .sum();
    }

}
