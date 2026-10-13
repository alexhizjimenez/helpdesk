package com.alexhiz.hexagonal.helpdesk.shared.infrastructure.config;

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
        DepartmentEntity department = seedDepartment("SOCIAL6");
        seedUsers(department, roles);
        seedSlas();
    }

    /**
     * Inicializa los permisos del sistema iterando sobre todos los valores de PermissionEnum.
     * Si un permiso no existe en la base de datos, lo crea y lo persiste.
     * 
     * @return Mapa asociativo de cada enum PermissionEnum con su respectiva PermissionEntity persistida.
     */
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

    /**
     * Define y persiste la matriz de roles y sus correspondientes permisos:
     * - USER: Permisos básicos para crear y gestionar sus propios tickets.
     * - AGENT: Permisos para reclamar, resolver y ver tickets asignados o de su equipo.
     * - SUPERVISOR: Permisos de agente más asignación/reasignación, métricas, SLAs y auditoría.
     * - ADMIN: Acceso total a todos los permisos del sistema.
     * 
     * Corrige el tipo de dato: se busca en roleRepository pasando directamente la instancia RoleEnum,
     * evitando pasar String ("USER") que genera error de incompatibilidad de tipos con Hibernate.
     * 
     * @param p Mapa con los permisos ya persistidos en base de datos.
     * @return Mapa de roles con sus entidades RoleEntity persistidas.
     */
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

    /**
     * Función utilitaria que convierte una lista variable de PermissionEnum en un Set de PermissionEntity,
     * recuperando las entidades ya persistidas desde el mapa en memoria.
     */
    private Set<PermissionEntity> perms(Map<PermissionEnum, PermissionEntity> p, PermissionEnum... names) {
        return Arrays.stream(names).map(p::get).collect(Collectors.toSet());
    }

    /**
     * Crea un departamento inicial si no existe en la base de datos (por ejemplo, "SOCIAL6")
     * y lo establece como activo por defecto.
     * 
     * @param name Nombre del departamento a crear o consultar.
     * @return Entidad DepartmentEntity persistida.
     */
    private DepartmentEntity seedDepartment(String name) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> departmentRepository.save(
                        DepartmentEntity.builder().name(name).active(true).build()));
    }

    /**
     * Registra los usuarios iniciales del sistema (un administrador y un usuario general),
     * vinculándolos a su departamento y asignándoles el rol correspondiente.
     * 
     * @param department Departamento al que pertenecerán los usuarios iniciales.
     * @param roles Mapa de roles para obtener las entidades de ADMIN y USER.
     */
    private void seedUsers(DepartmentEntity department, Map<RoleEnum, RoleEntity> roles) {
        createUserIfAbsent("alexhizdev", "Alexis Jimenez", "alexhizdev@gmail.com",
                "9511234567", department, roles.get(RoleEnum.ADMIN));
        createUserIfAbsent("antonio", "antonio joaquin", "antonio@gmail.com",
                "9713488633", department, roles.get(RoleEnum.USER));
    }

    /**
     * Registra un usuario si su nombre de usuario no existe previamente en la base de datos.
     * La contraseña se encripta de forma segura mediante BCrypt antes de almacenarla.
     */
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

    /**
     * Inicializa las políticas de SLA (Service Level Agreements) predeterminadas para los cuatro
     * niveles de prioridad: LOW, MEDIUM, HIGH y CRITICAL, especificando tiempos de respuesta y resolución en minutos.
     */
    private void seedSlas() {
        createSlaIfAbsent("SLA Low", SlaPriotity.LOW, "240", "1440");
        createSlaIfAbsent("SLA Medium", SlaPriotity.MEDIUM, "120", "720");
        createSlaIfAbsent("SLA High", SlaPriotity.HIGH, "60", "240");
        createSlaIfAbsent("SLA Critical", SlaPriotity.CRITICAL, "30", "120");
    }

    /**
     * Comprueba si ya existe una política de SLA para una prioridad dada; si no existe,
     * la construye y la almacena con su estado activo.
     */
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
