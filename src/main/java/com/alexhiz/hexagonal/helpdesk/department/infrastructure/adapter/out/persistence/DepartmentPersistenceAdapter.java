 package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DepartmentPersistenceAdapter implements DepartmentRepositoryPort {
    private final DepartmentRepository departmentRepository;
    private final DepartmentPersistenceMapper departmentPersistenceMapper;

    @Override
    public Department save(Department department) {
        DepartmentEntity departmentEntity = departmentPersistenceMapper.toEntity(department);
        DepartmentEntity saved = departmentRepository.save(departmentEntity);
        return departmentPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll().stream()
                .map(departmentPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Department> findById(UUID id) {
        return departmentRepository.findByPublicId(id).map(departmentPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return departmentRepository.existsByPublicId(id);
    }

    @Override
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    @Override
    public void delete(UUID id) {
        DepartmentEntity department = departmentRepository.findByPublicId(id).orElseThrow(()-> new DepartmentNotFoundException(id));
        department.setActive(false);
        department.setDeletedAt(LocalDateTime.now());
        departmentRepository.saveAndFlush(department);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return departmentRepository.existsByNameAndPublicIdNot(name, id);
    }

    @Override
    public PageResult<Department> findAllPages(PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        Page<DepartmentEntity> entityPage= departmentRepository.findAll(pageable);
        List<Department> list = entityPage.getContent().stream().map(departmentPersistenceMapper::toDomain).toList();
        return new PageResult<>(list, entityPage.getNumber(), entityPage.getSize(), entityPage.getTotalElements(), entityPage.getTotalPages());
    }

    @Override
    public Department restoreDepartment(UUID id) {
        DepartmentEntity department = departmentRepository.findByPublicId(id).orElseThrow(()-> new DepartmentNotFoundException(id));
        if (department.getDeletedAt() == null) {
            return departmentPersistenceMapper.toDomain(department);
        }
        department.setActive(true);
        department.setDeletedAt(null);
        DepartmentEntity saved = departmentRepository.saveAndFlush(department);
        return departmentPersistenceMapper.toDomain(saved);
    }
}
