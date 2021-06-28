package uk.gov.ons.ctp.integration.mockai.addressindex.model;

import lombok.Data;

/** Address Index query result splitting address into Census component fields */
@Data
public class AddressIndexRhPostcodeAddressDTO {

  private String uprn;

  private String formattedAddress;

  private String addressType;

  private String censusAddressType;

  private String censusEstabType;

  private String countryCode;

  private long confidenceScore;
}
