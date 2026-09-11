package com.alexhiz.hexagonal.helpdesk.category.application.port.in;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;

public interface PageCategoriesUseCase {
    PageResult<Category> execute(PageQuery pageQuery);
}
