package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    List<UserEntity> id(UUID id);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Override
    @EntityGraph(attributePaths = {"department"})
    Page<UserEntity> findAll(Pageable pageable);
}
