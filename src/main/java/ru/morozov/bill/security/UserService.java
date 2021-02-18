package ru.morozov.bill.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Slf4j
public class UserService {

    @Value("${users.url}")
    private String usersUrl;

    public UserDto getUser(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = usersUrl + "/user/" + userId;
            log.debug("Sent request to " + url);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put(HeadersAuthenticationFilter.USER_ID_HEADER, Arrays.asList(String.valueOf(userId)));
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<UserDto> result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UserDto.class);
            log.info("User found. Result: {}", result.getBody());
            return result.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("User not found by id: " + userId);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to find user by id: " + userId, e);
        }
    }
}
