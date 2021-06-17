package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressIndexAddressDTO {

  private String uprn;
  
  private String formattedAddress;
  
  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String townName;

  private String postcode;

  private String foundAddressType;

  private String censusAddressType;

  private String censusEstabType; // Holds value of EstabType.code

  private String countryCode;

  private String organisationName;
}
