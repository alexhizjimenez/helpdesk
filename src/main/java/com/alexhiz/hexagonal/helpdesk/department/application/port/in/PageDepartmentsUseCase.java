package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;

public interface PageDepartmentsUseCase {
    PageResult<Department> execute(PageQuery pageQuery);
}
