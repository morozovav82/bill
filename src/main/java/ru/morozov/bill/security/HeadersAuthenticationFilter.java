package ru.morozov.bill.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Optional.ofNullable;

@Slf4j
public class HeadersAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public final static String USER_ID_HEADER = "X-UserId";

    public HeadersAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response) {
        Authentication auth;

//        try {
            String userIdStr = ofNullable(request.getHeader(USER_ID_HEADER))
                    .orElseThrow(() -> new PreAuthenticatedCredentialsNotFoundException("Missing UserId header"));

            try {
                Long userId = Long.parseLong(userIdStr);
                auth = new PreauthenticatedToken(userId);
            } catch (NumberFormatException e) {
                throw new PreAuthenticatedCredentialsNotFoundException("Wrong UserId header");
            }
//        } catch (PreAuthenticatedCredentialsNotFoundException e) {
//            log.info(e.getMessage());
//            auth = new AnonymousAuthenticationToken("anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
//        }

        return getAuthenticationManager().authenticate(auth);
    }


    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        log.error("unsuccessfulAuthentication: " + failed.getMessage());
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(failed.getMessage());
    }
}
