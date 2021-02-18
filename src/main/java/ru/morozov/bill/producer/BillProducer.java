package ru.morozov.bill.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.morozov.bill.service.MessageService;
import ru.morozov.messages.PaymentRejectedMsg;
import ru.morozov.messages.PaymentSuccessfulMsg;

@Service
@Slf4j
public class BillProducer {

    @Autowired
    private MessageService messageService;

    @Value("${mq.PaymentSuccessful-topic}")
    private String paymentSuccessfulTopic;

    @Value("${mq.PaymentRejected-topic}")
    private String paymentRejectedTopic;

    public void sendPaymentSuccessfulMessage(PaymentSuccessfulMsg message){
        messageService.scheduleSentMessage(paymentSuccessfulTopic, null, message, PaymentSuccessfulMsg.class);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPaymentRejectedMessage(PaymentRejectedMsg message){
        messageService.scheduleSentMessage(paymentRejectedTopic, null, message, PaymentRejectedMsg.class);
    }
}