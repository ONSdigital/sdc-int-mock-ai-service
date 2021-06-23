package uk.gov.ons.ctp.integration.mockai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressesRhUprnRequestDTO {

  private String addresstype;

  private boolean historical = true;

  private boolean verbose = true;

  private String epoch;
}
