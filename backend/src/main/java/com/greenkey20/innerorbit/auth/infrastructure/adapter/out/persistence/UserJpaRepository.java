package com.greenkey20.innerorbit.auth.infrastructure.adapter.out.persistence;

import com.greenkey20.innerorbit.auth.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Spring Data JPA 레포지토리
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUsername(String username);
}
