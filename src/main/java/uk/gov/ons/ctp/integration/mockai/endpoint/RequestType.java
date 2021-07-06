package uk.gov.ons.ctp.integration.mockai.endpoint;

import lombok.Getter;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexUprnResultDTO;

@Getter
public enum RequestType {
  AI_RH_POSTCODE(
      "/addresses/rh/postcode/{postcode}", "POSTCODE", AddressIndexRhPostcodeResultsDTO.class),
  AI_PARTIAL("/addresses/partial", "INPUT", AddressIndexPartialResultsDTO.class),
  AI_POSTCODE("/addresses/postcode/{postcode}", "POSTCODE", AddressIndexPostcodeResultsDTO.class),
  AI_RH_UPRN("/addresses/rh/uprn/{uprn}", null, AddressIndexUprnResultDTO.class),
  AI_EQ("/addresses/eq", "INPUT", String.class);

  private String url;
  private String path;
  private String placeholderName;
  private Class<?> responseClass;

  private RequestType(String url, String placeholderName, Class<?> responseClass) {
    this.url = url;
    this.placeholderName = placeholderName;
    this.path = url.replaceAll("/\\{.*\\}", "");
    this.responseClass = responseClass;
  }
}
