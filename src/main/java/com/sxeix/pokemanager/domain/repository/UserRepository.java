package com.sxeix.pokemanager.domain.repository;

import com.sxeix.pokemanager.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Integer id);

    User save(User user);

    User getReferenceById(Integer id);

}
