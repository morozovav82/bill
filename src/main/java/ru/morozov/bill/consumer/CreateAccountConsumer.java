package ru.morozov.bill.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.morozov.bill.dto.NewAccountDto;
import ru.morozov.bill.repo.RedisRepository;
import ru.morozov.bill.service.BillService;
import ru.morozov.messages.UserRegisteredMsg;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "${active-mq.CreateAccount-topic}")
public class CreateAccountConsumer {

    private final BillService billService;
    private final RedisRepository redisRepository;

    @RabbitHandler
    public void receive(UserRegisteredMsg msg) {
        log.info("Received Message: {}", msg.toString());

        String idempotenceKey = msg.getUserId().toString();
        log.info("idempotenceKey={}", idempotenceKey);

        //idempotence check
        Object value = redisRepository.find(idempotenceKey);
        if (value != null) {
            log.info("Order has already been processed. IdempotenceKey=" + idempotenceKey);
            return;
        } else {
            redisRepository.add(idempotenceKey, idempotenceKey);
        }

        try {
            NewAccountDto accountDto = new NewAccountDto();
            accountDto.setUserId(msg.getUserId());
            billService.createAccount(accountDto);
        } catch (Exception e) {
            log.error("Failed to save account", e);
        }
    }
}
