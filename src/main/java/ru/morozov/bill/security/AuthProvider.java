package ru.morozov.bill.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.morozov.bill.repo.RedisRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthProvider implements AuthenticationProvider {

    private final UserService userService;
    private final RedisRepository redisRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        PreauthenticatedToken preauthenticatedToken = (PreauthenticatedToken) authentication;
        Long userId = preauthenticatedToken.getUserId();

        //check user in Cache
        String cacheKey = "UserId_" + userId;
        UserDto cacheUser = (UserDto) redisRepository.find(cacheKey);

        if (cacheUser != null) {
            log.info("User found in cache. UserId=" + userId);
        } else {
            //get user from DB
            log.info("Get user from UserService. UserId=" + userId);

            try {
                cacheUser = userService.getUser(userId);

                //save user in Cache
                redisRepository.add(cacheKey, cacheUser);
            } catch (Throwable e) {
                log.warn("User not found: " + e.getMessage());
            }
        }

        if (cacheUser == null) {
            throw new AuthenticationCredentialsNotFoundException("User not found by ID=" + userId);
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(cacheUser, null, Collections.emptyList());

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreauthenticatedToken.class);
    }
}
