package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexRhPostcodeResponseDTO {

  private String postcode;

  private ArrayList<AddressIndexRhPostcodeAddressDTO> addresses;

  private String filter;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;
}
