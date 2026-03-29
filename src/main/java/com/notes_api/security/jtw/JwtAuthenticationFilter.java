package com.notes_api.security.jtw;

import com.notes_api.security.UserPrincipalAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    private final JwtToPrincipalConverter jwtToPrincipalConverter;

    @Autowired
    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, JwtToPrincipalConverter jwtToPrincipalConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtToPrincipalConverter = jwtToPrincipalConverter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            extractTokenFromRequestHeader(request)
                    .map(jwtDecoder::decode)
                    .map(jwtToPrincipalConverter::convert)
                    .map(UserPrincipalAuthenticationToken::new)
                    .ifPresent(userPrincipalAuthenticationToken ->
                            SecurityContextHolder.getContext().setAuthentication(userPrincipalAuthenticationToken));
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Optional<String> extractTokenFromRequestHeader(HttpServletRequest request){

        var token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")){
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }
}