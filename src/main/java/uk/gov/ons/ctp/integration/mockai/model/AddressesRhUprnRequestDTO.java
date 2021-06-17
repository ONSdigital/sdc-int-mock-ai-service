package uk.gov.ons.ctp.integration.mockai.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressesRhUprnRequestDTO {

  @NotNull private String addresstype;

  private boolean historical = true;

  private boolean verbose = true;

  private String epoch;
}
