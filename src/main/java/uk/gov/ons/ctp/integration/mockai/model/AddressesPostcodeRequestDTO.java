package uk.gov.ons.ctp.integration.mockai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressesPostcodeRequestDTO {

  private int offset = 0;

  private int limit = 100;

  private String classificationfilter;

  private boolean historical = true;

  private boolean verbose = true;

  private String epoch;

  private boolean includeauxiliarysearch = false;
}
