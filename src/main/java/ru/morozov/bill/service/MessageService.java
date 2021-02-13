package ru.morozov.bill.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.morozov.bill.entity.Message;
import ru.morozov.bill.repo.MessageRepository;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @SneakyThrows
    public void scheduleSentMessage(String topic, String routingKey, Object message, Class className) {
        String json = objectMapper.writeValueAsString(message);

        Message messageEntity = new Message();
        messageEntity.setTopic(topic);
        messageEntity.setRoutingKey(routingKey);
        messageEntity.setMessage(json);
        messageEntity.setClassName(className.getName());

        messageRepository.save(messageEntity);
    }

    @Scheduled(fixedDelay = 100L)
    public void sentMessages() {
        log.info("Start sentMessages");

        List<Message> messages = messageRepository.findBySentIsNullOrderById();
        messages.forEach(i -> {
            boolean res = sendMessage(i.getTopic(), i.getRoutingKey(), i.getMessage(), i.getClassName());

            if (res) {
                try {
                    i.setSent(new Date());
                    messageRepository.save(i);
                } catch (Throwable e) {
                    log.error("Failed to save message info. ID=" + i.getId(), e);
                }
            }
        });
    }

    private boolean sendMessage(String topic, String routingKey, String message, String className) {
        try {
            Class clazz = Class.forName(className);
            Object messageObj = objectMapper.readValue(message, clazz);

            log.info("Attempting send message to Topic: "+ topic);
            if (routingKey == null)
                rabbitTemplate.convertAndSend(topic, messageObj);
            else
                rabbitTemplate.convertAndSend(topic, routingKey, messageObj);
            log.info("Message sent: {}", message);

            return true;
        } catch(Exception e){
            log.error("Failed to send message", e);
            return false;
        }
    }
}
