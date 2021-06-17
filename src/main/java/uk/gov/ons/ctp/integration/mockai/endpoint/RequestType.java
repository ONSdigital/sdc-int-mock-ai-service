package uk.gov.ons.ctp.integration.mockai.endpoint;

import lombok.Getter;
import uk.gov.ons.ctp.integration.mockai.addressIndex.model.AddressIndexSearchResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressIndex.model.AddressIndexSingleResultDTO;

@Getter
public enum RequestType {
  AI_RH_POSTCODE("/addresses/rh/postcode/{postcode}", "POSTCODE", AddressIndexSearchResultsDTO.class),
  AI_PARTIAL("/addresses/partial", "INPUT", AddressIndexSearchResultsDTO.class),
  AI_POSTCODE("/addresses/postcode/{postcode}", "POSTCODE", AddressIndexSearchResultsDTO.class),
  AI_RH_UPRN("/addresses/rh/uprn/{uprn}", null, AddressIndexSingleResultDTO.class);

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
