package com.greenkey20.innerorbit.auth.infrastructure.adapter.out.persistence;

import com.greenkey20.innerorbit.auth.application.port.out.UserRepository;
import com.greenkey20.innerorbit.auth.domain.model.User;
import com.greenkey20.innerorbit.auth.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository out port 구현체 — JPA 영속성 어댑터
 */
@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::toDomainModel);
    }

    private User toDomainModel(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .build();
    }
}
