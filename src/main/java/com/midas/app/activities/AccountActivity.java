package com.midas.app.activities;

import com.midas.app.models.Account;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.Optional;
import java.util.UUID;

@ActivityInterface
public interface AccountActivity {
  /**
   * saveAccount saves an account in the data store.
   *
   * @param account is the account to be saved
   * @return Account
   */
  @ActivityMethod
  Account saveAccount(Account account);

  /**
   * createPaymentAccount creates a payment account in the system or provider.
   *
   * @param account is the account to be created
   * @return Account
   */
  @ActivityMethod
  Account createPaymentAccount(Account account);

  /**
   * updateAccount updates an account in the data store.
   *
   * @param account is the account to be updated
   * @return Account
   */
  @ActivityMethod
  Account updateAccount(Account account);

  /**
   * updatePaymentAccount updates a payment account in the system or provider.
   *
   * @param account is the account to be updated
   * @return Account
   */
  @ActivityMethod
  Account updatePaymentsAccount(Account account);

  /**
   * getAccount get account details from DB.
   *
   * @param accountId is the account to be updated
   * @return Account
   */
  @ActivityMethod
  Optional<Account> getAccount(UUID accountId);
}
