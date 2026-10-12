package com.alexhiz.hexagonal.helpdesk.ticket.application.port.in;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketListItem;

public interface PageTicketsUseCase {
    PageResult<TicketListItem> execute(PageQuery pageQuery);
}
