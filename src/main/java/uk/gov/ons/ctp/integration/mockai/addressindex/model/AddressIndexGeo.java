package uk.gov.ons.ctp.integration.mockai.addressindex.model;

import lombok.Data;

/** This class holds the 'geo' data for address level objects. */
@Data
public class AddressIndexGeo {
  private double latitude;

  private double longitude;

  private long easting;

  private long northing;
}
