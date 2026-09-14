package com.inno.task.order_service.mapper;

import com.inno.task.order_service.dto.CreateOrderRequest;
import com.inno.task.order_service.dto.OrderItemDto;
import com.inno.task.order_service.dto.OrderResponse;
import com.inno.task.order_service.dto.UserDto;
import com.inno.task.order_service.entity.Order;
import com.inno.task.order_service.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "items", source = "items")
    Order toEntity(CreateOrderRequest request);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItem toItemEntity(OrderItemDto dto);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "items", qualifiedByName = "mapItems")
    OrderResponse toResponse(Order order, UserDto user);

    @Named("mapItems")
    default List<OrderItemDto> mapItems(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    default OrderItemDto toItemDto(OrderItem orderItem) {
        if (orderItem == null) return null;
        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(orderItem.getItem().getId());
        dto.setQuantity(orderItem.getQuantity());
        return dto;
    }
}