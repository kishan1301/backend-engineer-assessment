package com.midas.app.workflows;

import com.midas.app.activities.AccountActivity;
import com.midas.app.models.Account;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.common.v1.Payloads;
import io.temporal.api.failure.v1.ApplicationFailureInfo;
import io.temporal.api.failure.v1.Failure;
import io.temporal.common.RetryOptions;
import io.temporal.internal.worker.WorkflowExecutionException;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class UpdateAccountWorkflowImpl implements UpdateAccountWorkflow {

  private final RetryOptions retryoptions =
      RetryOptions.newBuilder()
          .setInitialInterval(Duration.ofSeconds(1))
          .setMaximumInterval(
              Duration.ofSeconds(2)) // set to 10 seconds for demo, default is 100 seconds
          .setBackoffCoefficient(2)
          .setMaximumAttempts(3)
          .build();
  private final ActivityOptions defaultActivityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .setRetryOptions(retryoptions)
          .build();

  private final Map<String, ActivityOptions> methodOptions =
      Map.of(
          UpdateAccountWorkflow.UPDATE_ACCOUNT_WORKFLOW,
          ActivityOptions.newBuilder().setHeartbeatTimeout(Duration.ofSeconds(3)).build());

  private final AccountActivity updateAccountActivity =
      Workflow.newActivityStub(AccountActivity.class, defaultActivityOptions, methodOptions);

  @Override
  public Account updateAccount(Account details) {
    log.info("Fetching account from DB with id: {}", details.getId());
    Optional<Account> persistedAccountOpt = updateAccountActivity.getAccount(details.getId());
    if (persistedAccountOpt.isEmpty())
      throw new WorkflowExecutionException(
          Failure.newBuilder()
              .setMessage(HttpStatus.NOT_FOUND.getReasonPhrase())
              .setApplicationFailureInfo(
                  ApplicationFailureInfo.newBuilder()
                      .setDetails(Payloads.newBuilder().build())
                      .build())
              .build());
    Account persistedAccount = persistedAccountOpt.get();
    log.info(
        "Initiating update account process in stripe with providerId: {}",
        persistedAccount.getProviderId());
    persistedAccount.setLastName(details.getLastName());
    persistedAccount.setFirstName(details.getFirstName());
    persistedAccount.setEmail(details.getEmail());
    Account paymentAccount = updateAccountActivity.updatePaymentsAccount(persistedAccount);
    log.info("Saving updated account in DB.");
    Account account = updateAccountActivity.saveAccount(paymentAccount);
    log.info("Account persisted in DB.");
    return account;
  }
}
