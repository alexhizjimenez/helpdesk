package com.alexhiz.hexagonal.helpdesk.user.application.port.in;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;

public interface PageUsersUseCase {
    PageResult<User> execute(PageQuery pageQuery);
}
