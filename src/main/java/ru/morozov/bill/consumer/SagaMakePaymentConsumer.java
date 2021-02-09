package ru.morozov.bill.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.morozov.bill.service.BillService;
import ru.morozov.messages.SagaMakePaymentMsg;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "${active-mq.SagaMakePayment-topic}")
public class SagaMakePaymentConsumer {

    private final BillService billService;

    @RabbitHandler
    public void receive(SagaMakePaymentMsg msg) {
        log.info("Received Message: {}", msg.toString());
        try {
            billService.withdrawMoney(msg.getOrderId(), msg.getUserId(), msg.getPriceTotal());
        } catch (Exception e) {
            log.error("Failed to save bill", e);
        }
    }
}
