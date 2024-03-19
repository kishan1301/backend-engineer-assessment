package com.midas.app.mappers;

import com.midas.app.models.Account;
import com.midas.app.providers.external.stripe.dto.Customer;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.generated.model.AccountDto;
import lombok.NonNull;

public class Mapper {
  // Prevent instantiation
  private Mapper() {}

  /**
   * toAccountDto maps an account to an account dto.
   *
   * @param account is the account to be mapped
   * @return AccountDto
   */
  public static AccountDto toAccountDto(@NonNull Account account) {
    var accountDto = new AccountDto();

    accountDto
        .id(account.getId())
        .firstName(account.getFirstName())
        .lastName(account.getLastName())
        .email(account.getEmail())
        .createdAt(account.getCreatedAt())
        .updatedAt(account.getUpdatedAt())
        .providerId(account.getProviderId())
        .providerType(AccountDto.ProviderTypeEnum.fromValue(account.getProviderType()));

    return accountDto;
  }

  public static CreateAccount toCreateAccount(@NonNull Account account) {
    return CreateAccount.builder()
        .email(account.getEmail())
        .firstName(account.getFirstName())
        .lastName(account.getLastName())
        .build();
  }

  public static Account toAccount(@NonNull Customer customer) {
    String[] firstAndLastName = customer.name().split(" ");
    Account.AccountBuilder accountBuilder = Account.builder();
    accountBuilder
        .providerId(customer.id())
        .providerType(AccountDto.ProviderTypeEnum.STRIPE.getValue())
        .email(customer.email())
        .firstName(customer.name());
    if (firstAndLastName.length > 0) accountBuilder.firstName(firstAndLastName[0]);
    if (firstAndLastName.length > 1) accountBuilder.lastName(firstAndLastName[1]);
    return accountBuilder.build();
  }
}
