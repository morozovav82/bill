package ru.morozov.bill.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
