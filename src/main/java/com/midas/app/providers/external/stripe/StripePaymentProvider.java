package com.midas.app.providers.external.stripe;

import com.midas.app.mappers.Mapper;
import com.midas.app.models.Account;
import com.midas.app.providers.external.stripe.dto.Customer;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import com.midas.app.providers.payment.StripeAccountRequest;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class StripePaymentProvider implements PaymentProvider {
  private final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);
  private static final String AUTH_TOKEN_FORMAT = "Basic %s";
  private static final String TOKEN_KEY = "auth_token";

  // TODO: Invalidate this cached token when properties are refreshed.
  private final Map<String, String> tokenMap = new ConcurrentHashMap<>();

  private final StripeConfiguration configuration;
  private final StripeClient stripeClient;

  /** providerName is the name of the payment provider */
  @Override
  public String providerName() {
    return "stripe";
  }

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(CreateAccount details) {
    Customer customer =
        stripeClient.createCustomer(
            getAuthHeaader(configuration.getApiKey()),
            new StripeAccountRequest(
                String.format("%s %s", details.getFirstName(), details.getLastName()),
                details.getEmail()),
            MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    logger.info("Account created with email[{}]", customer.email());
    return Mapper.toAccount(customer);
  }

  /**
   * updateAccount updates an existing account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account updateAccount(CreateAccount details) {
    Customer customer =
        stripeClient.updateCustomer(
            getAuthHeaader(configuration.getApiKey()),
            new StripeAccountRequest(
                String.format("%s %s", details.getFirstName(), details.getLastName()),
                details.getEmail()),
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            details.getUserId());
    logger.info("Account created with email[{}]", customer.email());
    return Mapper.toAccount(customer);
  }

  private String getAuthHeaader(String apiKey) {
    tokenMap.computeIfAbsent(
        TOKEN_KEY,
        key -> {
          logger.info("------ Setting auth in cache. ------");
          String authString = apiKey.concat(":");
          return String.format(
              AUTH_TOKEN_FORMAT, Base64.getEncoder().encodeToString(authString.getBytes()));
        });
    return tokenMap.get(TOKEN_KEY);
  }
}
