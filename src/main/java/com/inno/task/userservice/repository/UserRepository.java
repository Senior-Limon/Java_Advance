package com.inno.task.userservice.repository;

import com.inno.task.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findById(Long id);

    //jpql
    @Modifying
    @Query("UPDATE User u SET u.name = :name, u.surname = :surname WHERE u.id = :id")
    int updateNameAndSurnameById(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("surname") String surname);

    //nativ
    @Modifying
    @Query(value = "UPDATE users SET active = :active WHERE id = :id", nativeQuery = true)
    int setActiveStatus(@Param("id") Long id, @Param("active") Boolean active);

    //named
    Optional<User> findByEmail(String email);
}