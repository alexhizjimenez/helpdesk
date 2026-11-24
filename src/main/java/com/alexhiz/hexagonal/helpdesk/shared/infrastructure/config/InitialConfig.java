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
        DepartmentEntity depGeneral = seedDepartment("GENERAL");
        DepartmentEntity depRrhh = seedDepartment("RECURSOS_HUMANOS");
        DepartmentEntity depIt = seedDepartment("TECNOLOGIAS_DE_LA_INFORMACION");
        DepartmentEntity depFinanzas = seedDepartment("FINANZAS_Y_CONTABILIDAD");
        DepartmentEntity depVentas = seedDepartment("VENTAS_Y_COMERCIAL");
        DepartmentEntity depMarketing = seedDepartment("MARKETING_Y_COMUNICACION");
        DepartmentEntity depOperaciones = seedDepartment("OPERACIONES_Y_LOGISTICA");
        seedUsers(depGeneral, roles);
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
    		DepartmentEntity depRrhh = seedDepartment("RECURSOS_HUMANOS");
    		DepartmentEntity depIt = seedDepartment("TECNOLOGIAS_DE_LA_INFORMACION");
    		DepartmentEntity depFinanzas = seedDepartment("FINANZAS_Y_CONTABILIDAD");
    		DepartmentEntity depVentas = seedDepartment("VENTAS_Y_COMERCIAL");
    		DepartmentEntity depMarketing = seedDepartment("MARKETING_Y_COMUNICACION");
    		DepartmentEntity depOperaciones = seedDepartment("OPERACIONES_Y_LOGISTICA");
        
        createCategoryIfAbsent("Recursos Humanos", "Gestión de personal, talento humano, contratación y clima laboral.", depRrhh.getId());
        createCategoryIfAbsent("Tecnologías de la Información (IT)", "Soporte técnico, desarrollo de software, infraestructura y ciberseguridad.", depIt.getId());
        createCategoryIfAbsent("Finanzas y Contabilidad", "Control financiero, presupuestos, contabilidad, facturación y auditoría.", depFinanzas.getId());
        createCategoryIfAbsent("Ventas y Comercial", "Gestión de clientes, prospección, estrategias comerciales y cierre de ventas.", depVentas.getId());
        createCategoryIfAbsent("Marketing y Comunicación", "Publicidad, manejo de redes sociales, branding y relaciones públicas.", depMarketing.getId());
        createCategoryIfAbsent("Operaciones y Logística", "Cadena de suministro, control de inventarios y gestión de procesos operativos.", depOperaciones.getId());
    }

    /**
     * Comprueba si ya existe una categoría por su nombre; si no existe,
     * la construye y la almacena.
     */
    private void createCategoryIfAbsent(String name, String description, UUID department_id) {
        if (categoryRepository.existsByName(name)) return;
        categoryRepository.save(CategoryEntity.builder()
                .name(name)
                .departmentId(department_id)
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
                .department(department)
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
