package com.alexhiz.hexagonal.helpdesk.shared.infrastructure.config;

import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence.CategoryEntity;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence.CategoryRepository;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentEntity;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentRepository;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.PermissionEnum;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.PermissionEntity;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.PermissionRepository;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RoleEntity;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RoleRepository;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.out.persistence.SlaEntity;
import com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.out.persistence.SlaRepository;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence.UserEntity;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Componente de inicialización del sistema (Data Seeder / Runner).
 * Implementa CommandLineRunner para ejecutarse automáticamente una vez que el contexto de Spring Boot ha iniciado.
 * Se encarga de poblar la base de datos con información base inicial:
 * Permisos, Roles con sus asignaciones, Departamentos, Usuarios predeterminados y Políticas SLA.
 */
@Component
@RequiredArgsConstructor
public class InitialConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SlaRepository slaRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Punto de entrada principal ejecutado al arrancar la aplicación.
     * Toda la secuencia se ejecuta bajo una transacción (@Transactional) para asegurar consistencia;
     * orquesta el orden de creación respetando las dependencias de negocio (permisos -> roles -> departamentos -> usuarios -> SLAs).
     */
    @Override
    @Transactional(rollbackFor = {IOException.class, SQLException.class})
    public void run(String... args) throws Exception {
        Map<PermissionEnum, PermissionEntity> p = seedPermissions();
        Map<RoleEnum, RoleEntity> roles = seedRoles(p);
        seedDepartments();
        seedCategories();
        DepartmentEntity department = departmentRepository.findByName("IT").orElseThrow(()->new DepartmentNotFoundException("IT"));
        seedUsers(department, roles);
        seedSlas();

    }


    private Map<PermissionEnum, PermissionEntity> seedPermissions() {
        Map<PermissionEnum, PermissionEntity> map = new EnumMap<>(PermissionEnum.class);
        for (PermissionEnum name : PermissionEnum.values()) {
            PermissionEntity entity = permissionRepository.findByName(name)
                    .orElseGet(() -> permissionRepository.save(
                            PermissionEntity.builder().name(name).build()));
            map.put(name, entity);
        }
        return map;
    }

    private void seedCategories() {
        DepartmentEntity it = departmentRepository.findByName("IT").orElseThrow(()->new DepartmentNotFoundException("IT"));
        DepartmentEntity finance = departmentRepository.findByName("FINANCE").orElseThrow(()->new DepartmentNotFoundException("FINANCE"));
        DepartmentEntity hr = departmentRepository.findByName("HR").orElseThrow(()->new DepartmentNotFoundException("HR"));
        DepartmentEntity sales = departmentRepository.findByName("SALES").orElseThrow(()->new DepartmentNotFoundException("SALES"));
        DepartmentEntity operations = departmentRepository.findByName("OPERATIONS").orElseThrow(()->new DepartmentNotFoundException("OPERATIONS"));

        createCategoryIfAbsent("HARDWARE", "Gestión, mantenimiento, reparación y soporte de equipos de cómputo y dispositivos físicos.", it);
        createCategoryIfAbsent("SOFTWARE", "Instalación, actualización, desarrollo y solución de problemas en aplicaciones y sistemas operativos.",it);
        createCategoryIfAbsent("NETWORK", "Administración, conectividad, configuración y mantenimiento de redes locales, internet y telecomunicaciones.",it);
        createCategoryIfAbsent("ACCESS", "Control de accesos, gestión de credenciales, permisos de usuario y autenticación de sistemas.",it);
        createCategoryIfAbsent("SECURITY", "Ciberseguridad, protección de datos, prevención de amenazas y políticas de seguridad informática.",it);
        createCategoryIfAbsent("DATABASE", "Administración, respaldo, optimización y mantenimiento de bases de datos corporativas.",operations);
        createCategoryIfAbsent("OTHER", "Requerimientos, incidencias o solicitudes de soporte tecnológico general que no encajan en las demás categorías.",hr);
    }


    private void createCategoryIfAbsent(String name, String description, DepartmentEntity department) {
        if (categoryRepository.existsByName(name)) return;
        categoryRepository.save(CategoryEntity.builder()
                .name(name)
                .department(department)
                .build());
    }


    private Map<RoleEnum, RoleEntity> seedRoles(Map<PermissionEnum, PermissionEntity> p) {
        Map<RoleEnum, Set<PermissionEntity>> definition = new EnumMap<>(RoleEnum.class);

        definition.put(RoleEnum.USER, perms(p,
                PermissionEnum.TICKET_CREATE,
                PermissionEnum.TICKET_READ_OWN,
                PermissionEnum.TICKET_COMMENT_ADD,
                PermissionEnum.TICKET_CLOSE_OWN,
                PermissionEnum.TICKET_REOPEN));

        definition.put(RoleEnum.AGENT, perms(p,
                PermissionEnum.TICKET_READ_ASSIGNED,
                PermissionEnum.TICKET_CLAIM,
                PermissionEnum.TICKET_STATUS_UPDATE,
                PermissionEnum.TICKET_RESOLVE,
                PermissionEnum.TICKET_COMMENT_ADD,
                PermissionEnum.TICKET_READ_TEAM));

        definition.put(RoleEnum.SUPERVISOR, perms(p,
                PermissionEnum.TICKET_READ_ASSIGNED,
                PermissionEnum.TICKET_CLAIM,
                PermissionEnum.TICKET_STATUS_UPDATE,
                PermissionEnum.TICKET_RESOLVE,
                PermissionEnum.TICKET_ASSIGN,
                PermissionEnum.TICKET_REASSIGN,
                PermissionEnum.TICKET_READ_TEAM,
                PermissionEnum.METRICS_READ,
                PermissionEnum.PRIORITY_MANAGE,
                PermissionEnum.AUDIT_READ));
        definition.put(RoleEnum.ADMIN, new HashSet<>(p.values())); // todos los permisos

        Map<RoleEnum, RoleEntity> result = new EnumMap<>(RoleEnum.class);
        definition.forEach((roleName, permissions) -> {
            RoleEntity role = roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(
                            RoleEntity.builder().name(roleName).permissions(permissions).build()));
            result.put(roleName, role);
        });
        return result;
    }


    private Set<PermissionEntity> perms(Map<PermissionEnum, PermissionEntity> p, PermissionEnum... names) {
        return Arrays.stream(names).map(p::get).collect(Collectors.toSet());
    }


    private DepartmentEntity seedDepartment(String name) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> departmentRepository.save(
                        DepartmentEntity.builder().name(name).active(true).build()));
    }

    private void seedDepartments() {
        createDepartmentIfAbsent("IT");
        createDepartmentIfAbsent("FINANCE");
        createDepartmentIfAbsent("HR");
        createDepartmentIfAbsent("SALES");
        createDepartmentIfAbsent("OPERATIONS");
    }

    private void createDepartmentIfAbsent(String name) {
        if (departmentRepository.existsByName(name)) return;
        departmentRepository.save(DepartmentEntity.builder().name(name).build());
    }


    private void seedUsers(DepartmentEntity department, Map<RoleEnum, RoleEntity> roles) {
        createUserIfAbsent("alexhizdev", "Alexis Jimenez", "alexhizdev@gmail.com",
                "9511234567", department, roles.get(RoleEnum.ADMIN));
        createUserIfAbsent("antonio", "antonio joaquin", "antonio@gmail.com",
                "9713488633", department, roles.get(RoleEnum.USER));
    }


    private void createUserIfAbsent(String username, String fullName, String email,
                                    String phone, DepartmentEntity department, RoleEntity role) {
        if (userRepository.existsByUsername(username)) return;
        userRepository.save(UserEntity.builder()
                .fullName(fullName)
                .email(email)
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .phone(phone)
                .departmentId(department.getId())
                .roles(Set.of(role))
                .build());
    }


    private void seedSlas() {
        createSlaIfAbsent("SLA Low", SlaPriotity.LOW, "240", "1440");
        createSlaIfAbsent("SLA Medium", SlaPriotity.MEDIUM, "120", "720");
        createSlaIfAbsent("SLA High", SlaPriotity.HIGH, "60", "240");
        createSlaIfAbsent("SLA Critical", SlaPriotity.CRITICAL, "30", "120");
    }


    private void createSlaIfAbsent(String name, SlaPriotity priority,
                                   String responseMin, String resolutionMin) {
        if (slaRepository.existsByPriority(priority)) return;
        slaRepository.save(SlaEntity.builder()
                .name(name)
                .priority(priority)
                .responseTimeMinutes(responseMin)
                .resolutionTimeMinutes(resolutionMin)
                .active("true")
                .build());
    }
}
