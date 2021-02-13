package ru.morozov.bill.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.morozov.bill.service.MessageService;
import ru.morozov.messages.PaymentRejectedMsg;
import ru.morozov.messages.PaymentSuccessfulMsg;

@Component
@Slf4j
public class BillProducer {

    @Autowired
    private MessageService messageService;

    @Value("${active-mq.PaymentSuccessful-topic}")
    private String paymentSuccessfulTopic;

    @Value("${active-mq.PaymentRejected-topic}")
    private String paymentRejectedTopic;

    public void sendPaymentSuccessfulMessage(PaymentSuccessfulMsg message){
        messageService.scheduleSentMessage(paymentSuccessfulTopic, null, message, PaymentSuccessfulMsg.class);
    }

    public void sendPaymentRejectedMessage(PaymentRejectedMsg message){
        messageService.scheduleSentMessage(paymentRejectedTopic, null, message, PaymentRejectedMsg.class);
    }
}