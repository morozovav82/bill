package ru.morozov.bill.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class PaymentSuccessMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
}
