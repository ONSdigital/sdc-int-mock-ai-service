package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AddressIndexSingleResultDTO {

  private String apiVersion;
  
  private String dataVersion;

  private AddressIndexResponseDTO response;
  
  private ResponseStatusData status;

  private ArrayList<String> errors;
}
