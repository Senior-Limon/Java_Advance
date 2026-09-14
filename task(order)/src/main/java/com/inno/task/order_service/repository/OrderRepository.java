package com.inno.task.order_service.repository;

import com.inno.task.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserIdAndDeletedFalse(Long userId);

    Optional<Order> findByIdAndDeletedFalse(Long id);
}
