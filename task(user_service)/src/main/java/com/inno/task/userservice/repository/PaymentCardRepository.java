package com.inno.task.userservice.repository;

import com.inno.task.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository                                                                   //possibility for create specifications
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<PaymentCard> findByUserId(Long userId);

    //jpql
    @Query("SELECT COUNT(c) FROM PaymentCard c WHERE c.user.id = :userId AND c.active = true")
    long countActiveCardsByUserId(@Param("userId") Long userId);

    //native
    @Modifying
    @Query(value = "UPDATE payment_cards SET active = :active WHERE id = :id", nativeQuery = true)
    void setActiveStatus(@Param("id") Long id, @Param("active") Boolean active);

    Optional<PaymentCard> findByNumber(String number);
}