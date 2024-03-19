package com.midas.app.controllers;

import com.midas.app.mappers.Mapper;
import com.midas.app.models.Account;
import com.midas.app.services.AccountService;
import com.midas.generated.api.AccountsApi;
import com.midas.generated.model.AccountDto;
import com.midas.generated.model.CreateAccountDto;
import com.midas.generated.model.UpdateAccountDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AccountController implements AccountsApi {
  private final AccountService accountService;
  private final Logger logger = LoggerFactory.getLogger(AccountController.class);

  /**
   * POST /accounts : Create a new user account Creates a new user account with the given details
   * and attaches a supported payment provider such as &#39;stripe&#39;.
   *
   * @param createAccountDto User account details (required)
   * @return User account created (status code 201)
   */
  @Override
  public ResponseEntity<AccountDto> createUserAccount(CreateAccountDto createAccountDto) {
    logger.info("Creating account for user with email: {}", createAccountDto.getEmail());

    var account =
        accountService.createAccount(
            Account.builder()
                .firstName(createAccountDto.getFirstName())
                .lastName(createAccountDto.getLastName())
                .email(createAccountDto.getEmail())
                .providerType(createAccountDto.getProviderType().getValue())
                .build());

    return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.CREATED);
  }

  /**
   * GET /accounts : Get list of user accounts Returns a list of user accounts.
   *
   * @return List of user accounts (status code 200)
   */
  @Override
  public ResponseEntity<List<AccountDto>> getUserAccounts() {
    logger.info("Retrieving all accounts");

    var accounts = accountService.getAccounts();
    var accountsDto = accounts.stream().map(Mapper::toAccountDto).toList();

    return new ResponseEntity<>(accountsDto, HttpStatus.OK);
  }

  /**
   * PATCH /accounts/{accountId} : Update an existing user's account Updates an existing user's
   * account with the given details and attaches a supported payment provider such as 'stripe'.
   *
   * @param accountId accountId of the user. (required)
   * @param updateAccountDto User account details (required)
   * @return User account accepted for update. (status code 202) or Bad request (status code 400) or
   *     Unauthorized (status code 401) or Forbidden (status code 403) or Internal server error
   *     (status code 500)
   */
  @Override
  public ResponseEntity<AccountDto> updateUserAccount(
      String accountId, UpdateAccountDto updateAccountDto) {
    logger.info("Updating user with accountId: {}", accountId);
    var account =
        accountService.updateAccount(
            Account.builder()
                .firstName(updateAccountDto.getFirstName())
                .lastName(updateAccountDto.getLastName())
                .email(updateAccountDto.getEmail())
                .id(UUID.fromString(accountId))
                .build());
    return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.ACCEPTED);
  }
}
