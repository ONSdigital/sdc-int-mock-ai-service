package uk.gov.ons.ctp.integration.mockai.addressindex.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexPostcodeResultsDTO {

  private String apiVersion;

  private String dataVersion;

  private AddressIndexPostcodeResponseDTO response;

  private AddressIndexStatusDTO status;

  private ArrayList<String> errors;
}
