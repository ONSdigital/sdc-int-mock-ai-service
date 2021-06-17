package uk.gov.ons.ctp.integration.mockai.addressIndex.model;

import lombok.Data;

@Data
public class AddressIndexGeo {
  private double latitude;

  private double longitude;
  
  private long easting;
  
  private long northing;
}
