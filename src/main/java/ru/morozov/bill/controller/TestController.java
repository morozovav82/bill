package ru.morozov.bill.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.morozov.bill.service.MessageService;
import ru.morozov.messages.SagaMakePaymentMsg;
import ru.morozov.messages.SagaMakePaymentRollbackMsg;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @Autowired
    private MessageService messageService;

    @Value("${active-mq.SagaMakePayment-topic}")
    private String sagaMakePaymentTopic;

    @Value("${active-mq.SagaMakePaymentRollback-topic}")
    private String sagaMakePaymentRollbackTopic;

    @PostMapping("/sendSagaMakePaymentMsg")
    public void sendSagaMakePaymentMsg(@RequestBody SagaMakePaymentMsg message) {
        messageService.scheduleSentMessage(sagaMakePaymentTopic, null, message, SagaMakePaymentMsg.class);
    }

    @PostMapping("/sendSagaMakePaymentRollbackMsg")
    public void sendSagaMakePaymentRollbackMsg(@RequestBody SagaMakePaymentRollbackMsg message) {
        messageService.scheduleSentMessage(sagaMakePaymentRollbackTopic, null, message, SagaMakePaymentRollbackMsg.class);
    }
}
