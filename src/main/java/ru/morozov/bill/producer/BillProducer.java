package ru.morozov.bill.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.morozov.messages.PaymentRejectedMsg;
import ru.morozov.messages.PaymentSuccessfulMsg;

@Component
@Slf4j
public class BillProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${active-mq.PaymentSuccessful-topic}")
    private String paymentSuccessfulTopic;

    @Value("${active-mq.PaymentRejected-topic}")
    private String paymentRejectedTopic;

    private void sendMessage(String topic, Object message){
        try{
            log.info("Attempting send message to Topic: "+ topic);
            jmsTemplate.convertAndSend(topic, message);
            log.info("Message sent: {}", message);
        } catch(Exception e){
            log.error("Failed to send message", e);
        }
    }

    public void sendPaymentSuccessfulMessage(PaymentSuccessfulMsg message){
        sendMessage(paymentSuccessfulTopic, message);
    }

    public void sendPaymentRejectedMessage(PaymentRejectedMsg message){
        sendMessage(paymentRejectedTopic, message);
    }
}