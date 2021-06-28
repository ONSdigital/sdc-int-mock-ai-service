package uk.gov.ons.ctp.integration.mockai.addressindex.model;

import lombok.Data;

@Data
public class AddressIndexUprnResponseDTO {

  private AddressIndexUprnAddressDTO address;

  private String addressType;

  private String epoch;
}
