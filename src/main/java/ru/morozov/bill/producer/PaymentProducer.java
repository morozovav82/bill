package ru.morozov.bill.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.morozov.bill.messages.PaymentRejectMsg;
import ru.morozov.bill.messages.PaymentSuccessMsg;

@Component
@Slf4j
public class PaymentProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${active-mq.payment-success-topic}")
    private String successTopic;

    @Value("${active-mq.payment-reject-topic}")
    private String rejectTopic;

    public void sendSuccessMessage(PaymentSuccessMsg message){
        try{
            log.info("Attempting send message to Topic: "+ successTopic);
            jmsTemplate.convertAndSend(successTopic, message);
            log.info("Message sent: {}", message);
        } catch(Exception e){
            log.error("Failed to send message", e);
        }
    }

    public void sendRejectMessage(PaymentRejectMsg message){
        try{
            log.info("Attempting send message to Topic: "+ rejectTopic);
            jmsTemplate.convertAndSend(rejectTopic, message);
            log.info("Message sent: {}", message);
        } catch(Exception e){
            log.error("Failed to send message", e);
        }
    }
}