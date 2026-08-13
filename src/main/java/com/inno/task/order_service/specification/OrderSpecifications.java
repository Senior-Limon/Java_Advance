package com.inno.task.order_service.specification;

import com.inno.task.order_service.entity.Order;
import com.inno.task.order_service.entity.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

    public static Specification<Order> filterByDateAndStatus(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<OrderStatus> statuses) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("deleted")));

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}