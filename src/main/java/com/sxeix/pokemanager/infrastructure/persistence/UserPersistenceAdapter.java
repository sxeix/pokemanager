package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.model.User;
import com.sxeix.pokemanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    // TODO: should switch this out for redis
    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(final User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public User getReferenceById(Integer id) {
        return jpaUserRepository.getReferenceById(id);
    }

    @Override
    public Optional<User> findById(final Integer id) {
        return jpaUserRepository.findById(id);
    }

}
