package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.stream.Collectors;

@Repository
public class DepartmentPersistenceAdapter implements DepartmentRepositoryPort {
    private final DepartmentRepository departmentRepository;
    private final DepartmentPersistenceMapper departmentPersistenceMapper;

    public DepartmentPersistenceAdapter(DepartmentRepository departmentRepository, DepartmentPersistenceMapper departmentPersistenceMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentPersistenceMapper = departmentPersistenceMapper;
    }

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
        return departmentRepository.findById(id).map(departmentPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return departmentRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    @Override
    public void delete(UUID id) {
        departmentRepository.deleteById(id);
    }

    
}
