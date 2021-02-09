package ru.morozov.bill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Value("${active-mq.SagaMakePayment-topic}")
    private String sagaMakePaymentTopic;

    @Value("${active-mq.SagaMakePaymentRollback-topic}")
    private String sagaMakePaymentRollbackTopic;
    
    @Value("${active-mq.PaymentSuccessful-topic}")
    private String paymentSuccessfulTopic;

    @Value("${active-mq.PaymentRejected-topic}")
    private String paymentRejectedTopic;

    @Bean
    public Queue sagaMakePaymentQueue() {
        return new Queue(sagaMakePaymentTopic);
    }

    @Bean
    public Queue sagaMakePaymentRollbackQueue() {
        return new Queue(sagaMakePaymentRollbackTopic);
    }

    @Bean
    public Queue paymentSuccessfulQueue() {
        return new Queue(paymentSuccessfulTopic);
    }

    @Bean
    public Queue paymentRejectedQueue() {
        return new Queue(paymentRejectedTopic);
    }
}
