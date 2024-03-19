package com.midas.app.providers.external.stripe;

import com.midas.app.providers.external.stripe.dto.Customer;
import com.midas.app.providers.payment.StripeAccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "stripe", configuration = DefaultFeignConfig.class)
public interface StripeClient {

  @PostMapping(value = "v1/customers", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  Customer createCustomer(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
      @RequestBody StripeAccountRequest createAccountRequest,
      @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType);

  @PostMapping(
      value = "v1/customers/{customerId}",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  Customer updateCustomer(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
      @RequestBody StripeAccountRequest stripeAccountRequest,
      @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType,
      @PathVariable("customerId") String customerId);
}
