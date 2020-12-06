package ru.morozov.bill.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountDto extends NewAccountDto {
    private Long id;
    private Float balance;
}
