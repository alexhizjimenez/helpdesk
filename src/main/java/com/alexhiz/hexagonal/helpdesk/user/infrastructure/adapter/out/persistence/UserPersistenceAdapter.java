package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserPersistenceAdapter(UserRepository userRepository, UserPersistenceMapper userPersistenceMapper) {
        this.userRepository = userRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userPersistenceMapper.toEntity(user);
        UserEntity saved = userRepository.save(userEntity);
        return userPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
