package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        UserEntity saved = userRepository.save(userPersistenceMapper.toEntity(user));
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

    @Override
    public PageResult<User> findAllPages(PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        Page<UserEntity> entity = userRepository.findAll(pageable);
        List<User> list = entity.getContent().stream().map(userPersistenceMapper::toDomain).toList();
        return new PageResult<>(list,  entity.getNumber(), entity.getSize(), entity.getTotalElements(), entity.getTotalPages());
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return userRepository.existsByEmailAndIdNot(email,id);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
