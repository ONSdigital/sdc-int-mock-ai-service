package uk.gov.ons.ctp.integration.mockai.endpoint;

import lombok.Getter;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexUprnResultDTO;

@Getter
public enum RequestType {
  AI_RH_POSTCODE(
      "/addresses/rh/postcode/{postcode}",
      "Search for an address by postcode. RH version.",
      "POSTCODE",
      AddressIndexRhPostcodeResultsDTO.class,
      "offset",
      "limit"),
  AI_PARTIAL(
      "/addresses/partial",
      "Search by partial address.",
      "INPUT",
      AddressIndexPartialResultsDTO.class,
      "input",
      "offset",
      "limit"),
  AI_POSTCODE(
      "/addresses/postcode/{postcode}",
      "Search for an address by postcode.",
      "POSTCODE",
      AddressIndexPostcodeResultsDTO.class,
      "offset",
      "limit"),
  AI_RH_UPRN(
      "/addresses/rh/uprn/{uprn}",
      "Gets an address by UPRN.",
      null,
      AddressIndexUprnResultDTO.class),
  AI_EQ("/addresses/eq", "Search for address for type ahead.", "INPUT", String.class, "input");

  private String url;
  private String description;
  private String path;
  private String placeholderName; // For not-found response text
  private Class<?> responseClass;
  private String[] queryParams;

  private RequestType(
      String url,
      String description,
      String placeholderName,
      Class<?> responseClass,
      String... queryParams) {
    this.url = url;
    this.description = description;
    this.placeholderName = placeholderName;
    this.path = url.replaceAll("/\\{.*\\}", "");
    this.responseClass = responseClass;
    this.queryParams = queryParams;
  }
}
