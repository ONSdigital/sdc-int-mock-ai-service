package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressIndexResponseCompositeDTO {

  private String postcode;
  
  private ArrayList<AddressIndexAddressCompositeDTO> addresses;
  
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
