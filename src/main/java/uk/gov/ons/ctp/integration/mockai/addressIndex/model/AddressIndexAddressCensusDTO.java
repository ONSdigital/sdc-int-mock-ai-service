package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressIndexAddressCensusDTO {
  private String addressType;
  
  private String estabType;
  
  private String countryCode;
}
