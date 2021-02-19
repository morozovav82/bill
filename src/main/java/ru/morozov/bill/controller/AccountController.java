package ru.morozov.bill.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.morozov.bill.entity.Account;
import ru.morozov.bill.exceptions.NotFoundException;
import ru.morozov.bill.service.BillService;
import ru.morozov.bill.utils.AuthUtils;

@RestController()
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final BillService billService;

    @GetMapping("/{userId}")
    public ResponseEntity<Account> getAccount(@PathVariable("userId") Long userId) {
        try {
            return new ResponseEntity(
                    billService.findByUserId(userId),
                    HttpStatus.OK
            );
        } catch (NotFoundException e) {
            log.warn(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/depositMoney")
    public ResponseEntity<Account> depositMoney(@PathVariable("userId") Long userId, @RequestParam Float money) {
        if (!AuthUtils.getCurrentUserId().equals(userId)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        try {
            billService.depositMoney(userId, money);

            return new ResponseEntity(
                    billService.findByUserId(userId),
                    HttpStatus.OK
            );
        } catch (NotFoundException e) {
            log.warn(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
