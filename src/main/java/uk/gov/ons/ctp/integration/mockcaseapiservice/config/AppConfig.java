package uk.gov.ons.ctp.integration.mockcaseapiservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/** Application Config bean */
@EnableRetry
@Validated
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private AddressIndexConfig addressIndex;
}
