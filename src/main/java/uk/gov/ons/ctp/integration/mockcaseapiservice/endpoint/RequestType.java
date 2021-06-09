package uk.gov.ons.ctp.integration.mockcaseapiservice.endpoint;

import lombok.Getter;

@Getter
public enum RequestType {
  AI_RH_POSTCODE("/addresses/rh/postcode"),
  AI_PARTIAL("/addresses/partial"),
  AI_POSTCODE("/addresses/postcode"),
  AI_RH_UPRN("/addresses/rh/uprn");

  private String path;

  RequestType(String path) {
    this.path = path;
  }
}
