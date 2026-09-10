package com.inno.task.payment_service.repository;

import com.inno.task.payment_service.entity.Payment;
import com.inno.task.payment_service.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByUserIdOrOrderIdOrStatus(Long userId, Long orderId, PaymentStatus status);

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    @Aggregation("{ $match: { userId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }" +
            "{ $group: { _id: null, total: { $sum: '$paymentAmount' } } }")
    BigDecimal getTotalSumByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);

    @Aggregation("{ $match: { timestamp: { $gte: ?0, $lte: ?1 } } }" +
            "{ $group: { _id: null, total: { $sum: '$paymentAmount' } } }")
    BigDecimal getTotalSumForAllUsersByDateRange(LocalDateTime start, LocalDateTime end);

    Optional<Payment> findById(String id);
}