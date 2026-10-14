package com.alexhiz.hexagonal.helpdesk.auth.application.service;

import com.alexhiz.hexagonal.helpdesk.auth.application.port.in.LoginAuthUseCase;
import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Auth;
import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Login;
import com.alexhiz.hexagonal.helpdesk.shared.infrastructure.security.JwtUtils;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements LoginAuthUseCase {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public Auth login(Login login) {
        Authentication authentication = this.autenticate(login.username(), login.password());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.createToken(authentication);
        return new Auth(login.username(), "User  logged successfully", accessToken, true);
    }

    public Authentication autenticate(String username, String password){
        UserDetails udsi = userDetailsServiceImpl.loadUserByUsername(username);
        if(!passwordEncoder.matches(password, udsi.getPassword())){
            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }
        return new UsernamePasswordAuthenticationToken(username, password, udsi.getAuthorities());
    }
}
