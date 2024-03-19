package com.midas.app.providers.external.stripe;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {

  @Bean
  Decoder getDecoder() {
    return new JacksonDecoder(
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
  }

  @Bean
  Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
    return new FormEncoder(new SpringEncoder(messageConverters));
  }
}
