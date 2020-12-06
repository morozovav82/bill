package ru.morozov.bill.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderDto {
    private Long id;
    private Long userId;
    private Integer productId;
    private Integer qnt;
    private Float cost;
}
