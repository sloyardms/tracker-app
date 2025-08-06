package com.sloyardms.trackerapi.security;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthUtils {

    public static UUID getCurrentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if(principal instanceof String s) return UUID.fromString(s);
        if(principal instanceof Jwt jwt) return UUID.fromString(jwt.getSubject());
        if(principal instanceof OidcUser oidcUser) return UUID.fromString(oidcUser.getSubject());

        throw new IllegalStateException("Unknown principal type: " + principal.getClass());
    }

}
