package com.midas.app.workflows;

import com.midas.app.activities.AccountActivity;
import com.midas.app.models.Account;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateAccountWorkflowImpl implements CreateAccountWorkflow {

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
          CreateAccountWorkflow.CREATE_ACCOUNT_QUEUE_NAME,
          ActivityOptions.newBuilder().setHeartbeatTimeout(Duration.ofSeconds(3)).build());

  private final AccountActivity accountCreationActivity =
      Workflow.newActivityStub(AccountActivity.class, defaultActivityOptions, methodOptions);

  @Override
  public Account createAccount(Account details) {
    log.info("Initializing account creation in stripe with email: {}", details.getEmail());
    Account paymentAccount = accountCreationActivity.createPaymentAccount(details);
    log.info("Saving account in DB.");
    Account account = accountCreationActivity.saveAccount(paymentAccount);
    log.info("Account persisted in DB.");
    return account;
  }
}
