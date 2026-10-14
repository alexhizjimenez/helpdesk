package com.alexhiz.hexagonal.helpdesk.auth.application.port.in;

import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Auth;
import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Login;

public interface LoginAuthUseCase {
    Auth login(Login login);
}
