package uk.gov.ons.ctp.integration.mockai.addressIndex.model;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressIndexResponseDTO {

  private AddressIndexAddressDTO address;
  
  private String addressType;
  
  private String epoch; 
}
