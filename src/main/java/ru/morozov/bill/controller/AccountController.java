package ru.morozov.bill.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.morozov.bill.AccountMapper;
import ru.morozov.bill.dto.AccountDto;
import ru.morozov.bill.dto.NewAccountDto;
import ru.morozov.bill.entity.Account;
import ru.morozov.bill.repo.AccountRepository;

import java.util.Optional;

@RestController()
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(@RequestBody NewAccountDto account) {
        if (account.getUserId() == null || account.getUserId() < 1) {
            throw new RuntimeException("Empty userId");
        }

        return AccountMapper.convertAccountToAccountDto(
                accountRepository.save(
                        AccountMapper.convertNewAccountDtoToAccount(account)
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Account> getAccount(@PathVariable("userId") Long userId) {
        Optional<Account> res = accountRepository.findOneByUserId(userId);
        if (res.isPresent()) {
            return new ResponseEntity(
                    AccountMapper.convertAccountToAccountDto(res.get()),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/put")
    public ResponseEntity<Account> putMoney(@PathVariable("userId") Long userId, @RequestParam Float money) {
        Optional<Account> res = accountRepository.findOneByUserId(userId);
        if (res.isPresent()) {
            Account account = res.get();
            account.setBalance(account.getBalance() + money);
            accountRepository.save(account);

            return new ResponseEntity(
                    AccountMapper.convertAccountToAccountDto(account),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
