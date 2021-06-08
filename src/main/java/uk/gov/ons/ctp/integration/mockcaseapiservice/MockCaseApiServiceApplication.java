package uk.gov.ons.ctp.integration.mockcaseapiservice;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.common.rest.RestClientConfig;
import uk.gov.ons.ctp.integration.mockcaseapiservice.client.AddressIndexClient;
import uk.gov.ons.ctp.integration.mockcaseapiservice.config.AppConfig;

/** The 'main' entry point for the ContactCentre Svc SpringBoot Application. */
@SpringBootApplication
// @ComponentScan(basePackages = {"uk.gov.ons.ctp.integration.mockcaseapiservice"})
@EnableCaching
public class MockCaseApiServiceApplication {
  private static final Logger log = LoggerFactory.getLogger(MockCaseApiServiceApplication.class);

  @Autowired private AppConfig appConfig;

  
  /**
   * The main entry point for this application.
   *
   * @param args runtime command line args
   */
  public static void main(final String[] args) {

    SpringApplication.run(MockCaseApiServiceApplication.class, args);
  }

  /**
   * Custom Object Mapper
   *
   * @return a customer object mapper
   */
  @Bean
  @Primary
  public CustomObjectMapper customObjectMapper() {
    return new CustomObjectMapper();
  }

  /**
   * Bean used to map exceptions for endpoints
   *
   * @return the service client
   */
  @Bean
  public RestExceptionHandler restExceptionHandler() {
    return new RestExceptionHandler();
  }
  
  @Bean
  public AddressIndexClient addressIndexClient() throws CTPException {
    log.info("Address Index configuration: {}", appConfig.getAddressIndex());
    RestClientConfig clientConfig = appConfig.getAddressIndex().getRestClientConfig();
    var statusMapping = clientErrorMapping();
    RestClient restClient =
        new RestClient(clientConfig, statusMapping, HttpStatus.INTERNAL_SERVER_ERROR);
    
    String aiToken = appConfig.getAddressIndex().getToken();
    return new AddressIndexClient(restClient, aiToken);
  }
  
  private Map<HttpStatus, HttpStatus> clientErrorMapping() {
    Map<HttpStatus, HttpStatus> mapping = new HashMap<>();
    EnumSet.allOf(HttpStatus.class).stream()
        .filter(s -> s.is4xxClientError())
        .forEach(s -> mapping.put(s, s));
    return mapping;
  }
}
