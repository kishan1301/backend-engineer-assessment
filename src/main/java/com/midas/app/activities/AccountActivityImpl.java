package com.midas.app.activities;

import com.midas.app.models.Account;
import com.midas.app.providers.external.stripe.StripePaymentProvider;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.repositories.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

@Component
public class AccountActivityImpl implements AccountActivity {

  private final AccountRepository repository;
  private final StripePaymentProvider stripePaymentProvider;

  public AccountActivityImpl(
      AccountRepository repository, StripePaymentProvider stripePaymentProvider) {
    this.repository = repository;
    this.stripePaymentProvider = stripePaymentProvider;
  }

  /**
   * updateAccount updates an account in the data store.
   *
   * @param account is the account to be updated
   * @return Account
   */
  @Override
  public Account updateAccount(Account account) {
    return saveAccount(account);
  }

  /**
   * updatePaymentAccount updates a payment account in the system or provider.
   *
   * @param account is the account to be updated
   * @return Account
   */
  @Override
  public Account updatePaymentsAccount(Account account) {
    Account updatedAccount =
        stripePaymentProvider.updateAccount(
            CreateAccount.builder()
                .userId(account.getProviderId())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .build());
    account.setEmail(updatedAccount.getEmail());
    account.setFirstName(updatedAccount.getFirstName());
    account.setLastName(updatedAccount.getLastName());
    return account;
  }

  /**
   * saveAccount saves an account in the data store.
   *
   * @param account is the account to be saved
   * @return Account
   */
  @Override
  public Account saveAccount(Account account) {
    return repository.saveAndFlush(account);
  }

  /**
   * createPaymentAccount creates a payment account in the system or provider.
   *
   * @param account is the account to be created
   * @return Account
   */
  @Override
  public Account createPaymentAccount(Account account) {
    return stripePaymentProvider.createAccount(
        CreateAccount.builder()
            .email(account.getEmail())
            .firstName(account.getFirstName())
            .lastName(account.getLastName())
            .build());
  }

  /**
   * getAccount get account details from DB.
   *
   * @param accountId is the account to be updated
   * @return Account
   */
  @Override
  public Optional<Account> getAccount(UUID accountId) {
    ExampleMatcher matcher =
        ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
    return repository.findOne(Example.of(Account.builder().id(accountId).build(), matcher));
  }
}
