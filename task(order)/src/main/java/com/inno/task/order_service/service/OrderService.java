package com.inno.task.order_service.service;

import com.inno.task.order_service.client.UserServiceClient;
import com.inno.task.order_service.dto.CreateOrderRequest;
import com.inno.task.order_service.dto.OrderItemDto;
import com.inno.task.order_service.dto.OrderResponse;
import com.inno.task.order_service.dto.UserDto;
import com.inno.task.order_service.entity.*;
import com.inno.task.order_service.exception.*;
import com.inno.task.order_service.mapper.OrderMapper;
import com.inno.task.order_service.repository.ItemRepository;
import com.inno.task.order_service.repository.OrderRepository;
import com.inno.task.order_service.specification.OrderSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper mapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = mapper.toEntity(request);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDto itemDto : request.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException(itemDto.getItemId()));

            OrderItem orderItem = mapper.toItemEntity(itemDto);
            orderItem.setOrder(order);
            orderItem.setItem(item);
            order.addItem(orderItem);

            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);

        UserDto user = userServiceClient.getUserById(saved.getUserId());
        log.info("Created order {} for user {}", saved.getId(), saved.getUserId());

        return mapper.toResponse(saved, user);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        UserDto user = userServiceClient.getUserById(order.getUserId());
        return mapper.toResponse(order, user);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(LocalDateTime start, LocalDateTime end,
                                         List<OrderStatus> statuses, Pageable pageable) {
        Specification<Order> spec = OrderSpecifications.filterByDateAndStatus(start, end, statuses);
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);

        return ordersPage.map(order -> {
            UserDto user = userServiceClient.getUserById(order.getUserId());
            return mapper.toResponse(order, user);
        });
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdAndDeletedFalse(userId);

        UserDto user = userServiceClient.getUserById(userId);

        return orders.stream()
                .map(order -> mapper.toResponse(order, user))
                .toList();
    }

    @Transactional
    public OrderResponse updateOrder(Long id, CreateOrderRequest request) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new InvalidOrderStatusException(order.getStatus().name(), "UPDATE");
        }

        order.getItems().clear();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDto itemDto : request.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException(itemDto.getItemId()));
            OrderItem orderItem = mapper.toItemEntity(itemDto);
            orderItem.setOrder(order);
            orderItem.setItem(item);
            order.addItem(orderItem);
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        order.setTotalPrice(total);
        Order updated = orderRepository.save(order);

        UserDto user = userServiceClient.getUserById(updated.getUserId());
        log.info("Updated order {}", updated.getId());

        return mapper.toResponse(updated, user);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setDeleted(true);
        orderRepository.save(order);
        log.info("Soft deleted order {}", id);
    }
}