package ru.morozov.bill.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.morozov.bill.AccountMapper;
import ru.morozov.bill.dto.AccountDto;
import ru.morozov.bill.dto.NewAccountDto;
import ru.morozov.bill.entity.Account;
import ru.morozov.bill.exceptions.NotFoundException;
import ru.morozov.bill.producer.BillProducer;
import ru.morozov.bill.repo.AccountRepository;
import ru.morozov.messages.PaymentRejectedMsg;
import ru.morozov.messages.PaymentSuccessfulMsg;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillService {

    private final AccountRepository accountRepository;
    private final BillProducer billProducer;

    public AccountDto findByUserId(Long userId) {
        Optional<Account> res = accountRepository.findOneByUserId(userId);
        if (res.isPresent()) {
            Account account = res.get();
            return AccountMapper.convertAccountToAccountDto(account);
        } else {
            throw new NotFoundException(userId);
        }
    }

    public void withdrawMoney(Long orderId, Long userId, Float priceTotal) {
        Assert.notNull(orderId, "OrderId is null");
        Assert.notNull(userId, "UserId is null");
        Assert.notNull(priceTotal, "PriceTotal is null");
        Assert.isTrue(priceTotal > 0, "Wrong priceTotal");

        Optional<Account> res = accountRepository.findOneByUserId(userId);
        if (res.isPresent()) {
            Account account = res.get();

            float balance = account.getBalance() - priceTotal;
            if (balance >= 0F) {
                //set new balance
                account.setBalance(balance);
                accountRepository.save(account);
                log.info("Account updated: {}", AccountMapper.convertAccountToAccountDto(account));

                //send message to MQ
                billProducer.sendPaymentSuccessfulMessage(new PaymentSuccessfulMsg(orderId));
            } else {
                log.warn("Not enough money");

                //send message to MQ
                billProducer.sendPaymentRejectedMessage(new PaymentRejectedMsg(orderId));
            }
        } else {
            throw new RuntimeException("Account not found by user. OrderId=" + orderId + ", UserId=" + userId);
        }
    }

    public void depositMoney(Long userId, Float priceTotal) {
//        Assert.notNull(orderId, "OrderId is null");
        Assert.notNull(userId, "UserId is null");
        Assert.notNull(priceTotal, "PriceTotal is null");
        Assert.isTrue(priceTotal > 0, "Wrong priceTotal");

        Optional<Account> res = accountRepository.findOneByUserId(userId);
        if (res.isPresent()) {
            Account account = res.get();

            float balance = account.getBalance() + priceTotal;
            //set new balance
            account.setBalance(balance);
            accountRepository.save(account);
            log.info("Account updated: {}", AccountMapper.convertAccountToAccountDto(account));
        } else {
            throw new NotFoundException(userId);
        }
    }

    public AccountDto createAccount(NewAccountDto account) {
        Assert.notNull(account.getUserId(), "Empty userId");
        Assert.isTrue(account.getUserId() > 0, "Wrong userId");

        Optional<Account> res = accountRepository.findOneByUserId(account.getUserId());
        if (res.isPresent()) {
            throw new IllegalArgumentException("Account for userId=" + account.getUserId() + " exists");
        }

        return AccountMapper.convertAccountToAccountDto(
                accountRepository.save(
                        AccountMapper.convertNewAccountDtoToAccount(account)
                )
        );
    }
}
