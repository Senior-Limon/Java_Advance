package com.inno.task.service;

import com.inno.task.order_service.client.UserServiceClient;
import com.inno.task.order_service.dto.*;
import com.inno.task.order_service.entity.Item;
import com.inno.task.order_service.entity.Order;
import com.inno.task.order_service.entity.OrderItem;
import com.inno.task.order_service.exception.ItemNotFoundException;
import com.inno.task.order_service.mapper.OrderMapper;
import com.inno.task.order_service.repository.ItemRepository;
import com.inno.task.order_service.repository.OrderRepository;
import com.inno.task.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private OrderMapper mapper;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks private OrderService orderService;

    private CreateOrderRequest request;
    private Item mockItem;
    private Order mockOrder;
    private UserDto mockUser;

    @BeforeEach
    void setUp() {
        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setPrice(BigDecimal.TEN);

        mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setUserId(1L);

        mockUser = UserDto.builder().id(1L).name("Test").build();

        request = new CreateOrderRequest();
        request.setUserId(1L);

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setItemId(1L);
        itemDto.setQuantity(2);
        request.setItems(List.of(itemDto));
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setItem(mockItem);
        mockOrderItem.setQuantity(2);

        // Настраиваем маппер: он должен вернуть НЕ null, а наш мок-объект
        when(mapper.toItemEntity(any(OrderItemDto.class))).thenReturn(mockOrderItem);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(userServiceClient.getUserById(1L)).thenReturn(mockUser);
        when(mapper.toEntity(any(CreateOrderRequest.class))).thenReturn(mockOrder);
        when(mapper.toResponse(any(), any())).thenReturn(OrderResponse.builder().build());

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        verify(orderRepository).save(any(Order.class));
        verify(userServiceClient).getUserById(1L);
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any());
    }
}