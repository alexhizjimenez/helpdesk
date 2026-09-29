package com.alexhiz.hexagonal.helpdesk.shared.infrastructure.config;

import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentEntity;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentRepository;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.PermissionEnum;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.PermissionEntity;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.PermissionRepository;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RoleEntity;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InitialConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        PermissionEntity ticketCreate = PermissionEntity.builder().name(PermissionEnum.TICKET_CREATE).build();
        PermissionEntity ticketReadOwn = PermissionEntity.builder().name(PermissionEnum.TICKET_READ_OWN).build();
        PermissionEntity ticketCommentAdd = PermissionEntity.builder().name(PermissionEnum.TICKET_COMMENT_ADD).build();
        PermissionEntity ticketCloseOwn = PermissionEntity.builder().name(PermissionEnum.TICKET_CLOSE_OWN).build();
        PermissionEntity ticketReopen = PermissionEntity.builder().name(PermissionEnum.TICKET_REOPEN).build();

        PermissionEntity ticketReadAssigned = PermissionEntity.builder().name(PermissionEnum.TICKET_READ_ASSIGNED).build();
        PermissionEntity ticketClaim = PermissionEntity.builder().name(PermissionEnum.TICKET_CLAIM).build();
        PermissionEntity ticketStatusUpdate = PermissionEntity.builder().name(PermissionEnum.TICKET_STATUS_UPDATE).build();
        PermissionEntity ticketResolve = PermissionEntity.builder().name(PermissionEnum.TICKET_RESOLVE).build();

        PermissionEntity ticketAssign = PermissionEntity.builder().name(PermissionEnum.TICKET_ASSIGN).build();
        PermissionEntity ticketReassign = PermissionEntity.builder().name(PermissionEnum.TICKET_REASSIGN).build();
        PermissionEntity ticketReadTeam = PermissionEntity.builder().name(PermissionEnum.TICKET_READ_TEAM).build();
        PermissionEntity metricsRead = PermissionEntity.builder().name(PermissionEnum.METRICS_READ).build();
        PermissionEntity priorityManage = PermissionEntity.builder().name(PermissionEnum.PRIORITY_MANAGE).build();

        PermissionEntity userManage = PermissionEntity.builder().name(PermissionEnum.USER_MANAGE).build();
        PermissionEntity agentManage = PermissionEntity.builder().name(PermissionEnum.AGENT_MANAGE).build();
        PermissionEntity categoryManage = PermissionEntity.builder().name(PermissionEnum.CATEGORY_MANAGE).build();
        PermissionEntity departmentManage = PermissionEntity.builder().name(PermissionEnum.DEPARTMENT_MANAGE).build();
        PermissionEntity slaManage = PermissionEntity.builder().name(PermissionEnum.SLA_MANAGE).build();
        PermissionEntity auditRead = PermissionEntity.builder().name(PermissionEnum.AUDIT_READ).build();

        permissionRepository.saveAll(List.of(
                ticketCreate,
                ticketReadOwn,
                ticketCommentAdd,
                ticketCloseOwn,
                ticketReopen,
                ticketReadAssigned,
                ticketClaim,
                ticketStatusUpdate,
                ticketResolve,
                ticketAssign,
                ticketReassign,
                ticketReadTeam,
                metricsRead,
                priorityManage,
                userManage,
                agentManage,
                categoryManage,
                departmentManage,
                slaManage,
                auditRead
        ));

        RoleEntity admin = RoleEntity.builder().name(RoleEnum.ADMIN).permissions(Set.of(ticketCreate,
                ticketReadOwn,
                ticketCommentAdd,
                ticketCloseOwn,
                ticketReopen,
                ticketReadAssigned,
                ticketClaim,
                ticketStatusUpdate,
                ticketResolve,
                ticketAssign,
                ticketReassign,
                ticketReadTeam,
                metricsRead,
                priorityManage,
                userManage,
                agentManage,
                categoryManage,
                departmentManage,
                slaManage,
                auditRead)).build();
        RoleEntity agent = RoleEntity.builder().name(RoleEnum.AGENT).permissions(Set.of(
                ticketReadAssigned,
                ticketClaim,
                ticketStatusUpdate,
                ticketResolve,
                ticketCommentAdd,
                ticketReadTeam
        )).build();
        RoleEntity supervisor = RoleEntity.builder().name(RoleEnum.SUPERVISOR).permissions(Set.of(ticketReadAssigned,
                ticketClaim,
                ticketStatusUpdate,
                ticketResolve,
                ticketAssign,
                ticketReassign,
                ticketReadTeam,
                metricsRead,
                priorityManage,
                auditRead)).build();
        RoleEntity user = RoleEntity.builder().name(RoleEnum.USER).permissions(Set.of( ticketCreate,
                ticketReadOwn,
                ticketCommentAdd,
                ticketCloseOwn,
                ticketReopen)).build();
        roleRepository.saveAll(List.of(admin,agent,supervisor,user));

        DepartmentEntity departmentEntity = DepartmentEntity.builder().name("SOCIAL6").active(true).build();
        departmentRepository.save(departmentEntity);

    }
}
