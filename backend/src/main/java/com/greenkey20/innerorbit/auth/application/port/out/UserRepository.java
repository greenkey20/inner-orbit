package com.greenkey20.innerorbit.auth.application.port.out;

import com.greenkey20.innerorbit.auth.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * User 영속성 out port
 */
public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User user);

    List<User> findAll();
}
