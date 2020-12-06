package ru.morozov.bill.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.morozov.bill.AccountMapper;
import ru.morozov.bill.dto.OrderDto;
import ru.morozov.bill.entity.Account;
import ru.morozov.bill.messages.PaymentRejectMsg;
import ru.morozov.bill.messages.PaymentSuccessMsg;
import ru.morozov.bill.producer.PaymentProducer;
import ru.morozov.bill.repo.AccountRepository;
import ru.morozov.bill.service.OrderService;
import ru.morozov.order.messages.OrderCreatedMsg;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedConsumer implements MessageListener {

    private final AccountRepository accountRepository;
    private final PaymentProducer paymentProducer;
    private final OrderService orderService;

    @Override
    @JmsListener(destination = "${active-mq.order-created-topic}")
    public void onMessage(Message message) {
        ObjectMessage objectMessage;

        try {
            objectMessage = (ObjectMessage) message;
            log.info("Received Message: {}", objectMessage.getObject().toString());
        } catch (Exception e) {
            log.error("Failed to receive message", e);
            return;
        }

        if (objectMessage != null) {
            try {
                OrderCreatedMsg orderMsg = (OrderCreatedMsg) objectMessage.getObject();

                //get order info
                OrderDto order;
                try {
                    order = orderService.getOrder(orderMsg.getOrderId());
                 } catch (Throwable e) {
                    log.error("Failed to get order by id " + orderMsg.getOrderId(), e);
                    return;
                }

                Assert.notNull(order, "Order is empty");
                Assert.notNull(order.getUserId(), "UserId is empty");

                //get account
                Optional<Account> res = accountRepository.findOneByUserId(order.getUserId());
                if (res.isPresent()) {
                    Account account = res.get();

                    float balance = account.getBalance() - order.getCost();
                    if (balance >= 0F) {
                        //set new balance
                        account.setBalance(balance);
                        accountRepository.save(account);
                        log.info("Account updated: {}", AccountMapper.convertAccountToAccountDto(account));

                        //send message to MQ
                        paymentProducer.sendSuccessMessage(new PaymentSuccessMsg(order.getId()));
                    } else {
                        log.warn("Not enough money");

                        //send message to MQ
                        paymentProducer.sendRejectMessage(new PaymentRejectMsg(order.getId()));
                    }
                } else {
                    log.error("Account not found by userId: {}", order.getUserId());
                    return;
                }
            } catch (Exception e) {
                log.error("Failed to save order", e);
            }
        }
    }
}
