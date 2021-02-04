package ru.morozov.bill.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.morozov.bill.service.BillService;
import ru.morozov.messages.SagaMakePaymentRollbackMsg;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class SagaMakePaymentRollbackConsumer implements MessageListener {

    private final BillService billService;

    private ObjectMessage receiveMessage(Message message) {
        ObjectMessage objectMessage;

        try {
            objectMessage = (ObjectMessage) message;
            log.info("Received Message: {}", objectMessage.getObject().toString());
            return objectMessage;
        } catch (Exception e) {
            log.error("Failed to receive message", e);
            return null;
        }
    }

    @Override
    @JmsListener(destination = "${active-mq.SagaMakePaymentRollback-topic}")
    public void onMessage(Message message) {
        ObjectMessage objectMessage = receiveMessage(message);
        if (objectMessage == null) return;

        try {
            SagaMakePaymentRollbackMsg msg = (SagaMakePaymentRollbackMsg) objectMessage.getObject();
            billService.depositMoney(msg.getOrderId(), msg.getUserId(), msg.getPriceTotal());
        } catch (Exception e) {
            log.error("Failed to save bill", e);
        }
    }
}
