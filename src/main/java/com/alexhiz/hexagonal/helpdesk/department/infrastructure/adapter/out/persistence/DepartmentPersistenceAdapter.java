package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return departmentRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public PageResult<Department> findAllPages(PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        Page<DepartmentEntity> entityPage= departmentRepository.findAll(pageable);
        List<Department> list = entityPage.getContent().stream().map(departmentPersistenceMapper::toDomain).toList();
        return new PageResult<>(list, entityPage.getNumber(), entityPage.getSize(), entityPage.getTotalElements(), entityPage.getTotalPages())
;    }
}
