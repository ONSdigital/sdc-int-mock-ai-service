package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AddressIndexSearchResultsDTO {
  
  private String apiVersion;

  private String dataVersion;

  private AddressIndexResponseCompositeDTO response;

  private ResponseStatusData status;

  private ArrayList<String> errors;
}
