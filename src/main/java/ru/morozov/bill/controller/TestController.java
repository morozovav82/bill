package ru.morozov.bill.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.morozov.bill.service.MessageService;
import ru.morozov.messages.SagaMakePaymentMsg;
import ru.morozov.messages.SagaMakePaymentRollbackMsg;
import ru.morozov.messages.UserRegisteredMsg;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @Autowired
    private MessageService messageService;

    @Value("${mq.SagaMakePayment-topic}")
    private String sagaMakePaymentTopic;

    @Value("${mq.SagaMakePaymentRollback-topic}")
    private String sagaMakePaymentRollbackTopic;

    @Value("${mq.UserRegistered-exchange}")
    private String userRegisteredExchange;

    @PostMapping("/sendSagaMakePaymentMsg")
    public void sendSagaMakePaymentMsg(@RequestBody SagaMakePaymentMsg message) {
        messageService.scheduleSentMessage(sagaMakePaymentTopic, null, message, SagaMakePaymentMsg.class);
    }

    @PostMapping("/sendSagaMakePaymentRollbackMsg")
    public void sendSagaMakePaymentRollbackMsg(@RequestBody SagaMakePaymentRollbackMsg message) {
        messageService.scheduleSentMessage(sagaMakePaymentRollbackTopic, null, message, SagaMakePaymentRollbackMsg.class);
    }

    @PostMapping("/sendUserRegisteredMessage")
    public void sendUserRegisteredMessage(@RequestBody UserRegisteredMsg message) {
        Assert.notNull(message.getUserId(), "Empty userId");
        messageService.scheduleSentMessage(userRegisteredExchange, "default", message, UserRegisteredMsg.class);
    }
}
