package ru.morozov.order.messages;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class OrderCreatedMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
}
