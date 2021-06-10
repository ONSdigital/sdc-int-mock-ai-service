package uk.gov.ons.ctp.integration.mockai.endpoint;

import lombok.Getter;

@Getter
public enum RequestType {
  AI_RH_POSTCODE("/addresses/rh/postcode/{postcode}"),
  AI_PARTIAL("/addresses/partial"),
  AI_POSTCODE("/addresses/postcode/{postcode}"),
  AI_RH_UPRN("/addresses/rh/uprn/{uprn}");

  private String url;
  private String path;

  private RequestType(String url) {
      this.url = url;
      
      this.path = url.replaceAll("/\\{.*\\}", "");
      System.out.println("url: " + url);
      System.out.println("path: " + path);
    }
}
