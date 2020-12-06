package ru.morozov.bill;

import ru.morozov.bill.dto.NewAccountDto;
import ru.morozov.bill.dto.AccountDto;
import ru.morozov.bill.entity.Account;

public class AccountMapper {

    public static AccountDto convertAccountToAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setUserId(account.getUserId());
        accountDto.setBalance(account.getBalance());

        return accountDto;
    }

    public static Account convertNewAccountDtoToAccount(NewAccountDto newAccountDto) {
        Account account = new Account();
        account.setUserId(newAccountDto.getUserId());
        account.setBalance(0F);

        return account;
    }

    public static Account convertAccountDtoToAccount(AccountDto accountDto) {
        Account account = convertNewAccountDtoToAccount(accountDto);
        account.setId(accountDto.getId());

        return account;
    }
}
