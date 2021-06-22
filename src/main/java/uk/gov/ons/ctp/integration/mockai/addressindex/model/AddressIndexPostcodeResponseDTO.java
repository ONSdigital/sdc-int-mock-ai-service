package uk.gov.ons.ctp.integration.mockai.addressindex.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexPostcodeResponseDTO {

  private String postcode;

  private ArrayList<AddressIndexPostcodeAddressDTO> addresses;

  private String filter;

  private boolean historical;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;

  private boolean verbose;

  private boolean includeauxiliarysearch;
}
