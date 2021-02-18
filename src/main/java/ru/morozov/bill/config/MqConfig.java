package ru.morozov.bill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Value("${mq.SagaMakePayment-topic}")
    private String sagaMakePaymentTopic;

    @Value("${mq.SagaMakePaymentRollback-topic}")
    private String sagaMakePaymentRollbackTopic;
    
    @Value("${mq.PaymentSuccessful-topic}")
    private String paymentSuccessfulTopic;

    @Value("${mq.PaymentRejected-topic}")
    private String paymentRejectedTopic;

    @Value("${mq.UserRegistered-exchange}")
    private String userRegisteredExchange;

    @Value("${mq.CreateAccount-topic}")
    private String createAccountTopic;

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

    @Bean
    TopicExchange userRegisteredExchange() {
        return new TopicExchange(userRegisteredExchange);
    }

    @Bean
    public Queue createAccountQueue() {
        return new Queue(createAccountTopic);
    }

    @Bean
    Binding binding(@Qualifier("createAccountQueue") Queue queue, @Qualifier("userRegisteredExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("default");
    }
}
