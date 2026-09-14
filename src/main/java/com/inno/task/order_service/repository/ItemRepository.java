package com.inno.task.order_service.repository;

import com.inno.task.order_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
