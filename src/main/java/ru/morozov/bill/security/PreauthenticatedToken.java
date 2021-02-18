package ru.morozov.bill.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PreauthenticatedToken extends UsernamePasswordAuthenticationToken {

    @Getter
    private Long userId;

    public PreauthenticatedToken(Long userId) {
        super(null, null);
        this.userId = userId;
    }
}
