package com.sxeix.pokemanager.domain.service;

import com.sxeix.pokemanager.CreateUserRequest;
import com.sxeix.pokemanager.CreateUserResponse;
import com.sxeix.pokemanager.domain.model.User;
import com.sxeix.pokemanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userPersistenceAdapter;
    private final IdempotencyService idempotencyService;

    @Transactional(readOnly = true)
    public Optional<User> findUserById(final int id) {
        return userPersistenceAdapter.findById(id);
    }

    @Transactional
    public CreateUserResponse create(final CreateUserRequest createUserRequest) {

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setEmail(createUserRequest.getEmail());

        User createdUser = userPersistenceAdapter.save(user);

        CreateUserResponse response = CreateUserResponse.newBuilder()
                .setId(createdUser.getId())
                .build();

        // Update Idempotent Record (Within same transaction)
        idempotencyService.complete(createUserRequest.getIdempotencyKey(), response.toByteArray());

        // Transactional Outbox: Save event to a table
        // This ensures the event is sent ONLY if the user is created.
        // outboxService.saveEvent(new UserCreatedEvent(user.getId()));
        return response;
    }

    public User getReferenceById(Integer id) {
        return userPersistenceAdapter.getReferenceById(id);
    }

}
