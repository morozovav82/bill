package ru.morozov.bill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.morozov.bill.dto.OrderDto;

@Service
@Slf4j
public class OrderService {

    @Value("${order.url}")
    private String orderUrl;

    public OrderDto getOrder(Long orderId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = orderUrl + "/order/" + orderId;
            log.debug("Sent request to " + url);
            ResponseEntity<OrderDto> result = restTemplate.getForEntity(url, OrderDto.class);
            log.info("Order found. Result: {}", result.getBody());
            return result.getBody();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get order by id " + orderId, e);
        }
    }
}
