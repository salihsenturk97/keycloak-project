package com.salihapp.keycloak.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    private static final String CLAIM_ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";


    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();
    private final KeyCloakConfiguration properties;

    public JwtAuthConverter(KeyCloakConfiguration properties){
        this.properties = properties;
    }

    @Override
    @NonNull
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.of(
                        Optional.of(jwtGrantedAuthoritiesConverter.convert(jwt)).orElse(Collections.emptyList()),
                        Optional.ofNullable(extractRoles(jwt)).orElse(Collections.emptyList())
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
    }

    private String getPrincipalName(Jwt jwt) {
        return jwt.getClaim(Optional.ofNullable(properties.getPrincipalAttribute()).orElse(JwtClaimNames.SUB));
    }

    private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(CLAIM_RESOURCE_ACCESS);

        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        Object resourceObj = resourceAccess.get(properties.getResourceId());
        if (!(resourceObj instanceof Map<?, ?> rawResource)) {
            return Collections.emptySet();
        }

        Map<String, Object> resource = rawResource.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof String)
                .collect(Collectors.toMap(
                        entry -> (String) entry.getKey(),
                        Map.Entry::getValue
                ));

        Object rolesObj = resource.get(CLAIM_ROLES);
        if (!(rolesObj instanceof Collection<?> rawRoles)) {
            return Collections.emptySet();
        }

        return rawRoles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toUnmodifiableSet());
    }

}