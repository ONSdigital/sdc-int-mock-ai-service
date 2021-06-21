package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import lombok.Data;

/** Holds 'highlights' data for addresses returned by partial searches. */
@Data
public class AddressIndexHighlights {
  private String bestMatchAddress;

  private String source;

  private String lang;
}
