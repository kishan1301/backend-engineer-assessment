package com.midas.app.config;

import com.midas.app.activities.AccountActivity;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.CreateAccountWorkflowImpl;
import com.midas.app.workflows.UpdateAccountWorkflow;
import com.midas.app.workflows.UpdateAccountWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

  @Value("${temporal.target.host}")
  private String temporalHost;

  @Value("${temporal.target.port}")
  private String temporalPort;

  private final AccountActivity accountActivity;

  public TemporalConfig(AccountActivity accountActivity) {
    this.accountActivity = accountActivity;
  }

  @PostConstruct
  public void registerAndStartWorkflows() {
    var workflowServiceStubs =
        WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setEnableHttps(false)
                .setTarget(temporalHost.concat(":").concat(temporalPort))
                .build());
    startCreateWorkflow(workflowServiceStubs);
    startUpdateWorkflow(workflowServiceStubs);
  }

  private void startUpdateWorkflow(WorkflowServiceStubs workflowServiceStubs) {
    WorkerFactory workerFactory =
        WorkerFactory.newInstance(WorkflowClient.newInstance(workflowServiceStubs));
    Worker worker = workerFactory.newWorker(UpdateAccountWorkflow.UPDATE_ACCOUNT_WORKFLOW);
    worker.registerWorkflowImplementationTypes(UpdateAccountWorkflowImpl.class);
    worker.registerActivitiesImplementations(accountActivity);
    workerFactory.start();
  }

  private void startCreateWorkflow(WorkflowServiceStubs workflowServiceStubs) {
    WorkerFactory workerFactory =
        WorkerFactory.newInstance(WorkflowClient.newInstance(workflowServiceStubs));
    Worker worker = workerFactory.newWorker(CreateAccountWorkflow.CREATE_ACCOUNT_QUEUE_NAME);
    worker.registerWorkflowImplementationTypes(CreateAccountWorkflowImpl.class);
    worker.registerActivitiesImplementations(accountActivity);
    workerFactory.start();
  }
}
