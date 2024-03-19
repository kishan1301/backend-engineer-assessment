package com.midas.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.midas.app.models.Account;
import com.midas.app.workflows.CreateAccountWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @InjectMocks AccountServiceImpl accountService;
  @Mock WorkflowClient workflowClient;
  @Mock CreateAccountWorkflow createAccountWorkflow;

  @Test
  void createAccount() {
    Account details = Account.builder().email("sample@mail").build();
    var options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(CreateAccountWorkflow.CREATE_ACCOUNT_QUEUE_NAME)
            .setWorkflowId(details.getEmail())
            .build();
    when(workflowClient.newWorkflowStub(any(), any(WorkflowOptions.class)))
        .thenReturn(createAccountWorkflow);
    accountService.createAccount(details);
    verify(workflowClient, times(1)).newWorkflowStub(CreateAccountWorkflow.class, options);
    verify(createAccountWorkflow, times(1)).createAccount(details);
  }

  @Test
  void getAccounts() {
    Assertions.assertNotNull(accountService.getAccounts());
  }

  @Test
  void updateAccount() {}
}
