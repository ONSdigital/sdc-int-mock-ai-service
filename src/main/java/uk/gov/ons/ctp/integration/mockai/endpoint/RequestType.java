package uk.gov.ons.ctp.integration.mockai.endpoint;

import lombok.Getter;

@Getter
public enum RequestType {
  AI_RH_POSTCODE("/addresses/rh/postcode/{postcode}", "POSTCODE"),
  AI_PARTIAL("/addresses/partial", "INPUT"),
  AI_POSTCODE("/addresses/postcode/{postcode}", "POSTCODE"),
  AI_RH_UPRN("/addresses/rh/uprn/{uprn}", null);

  private String url;
  private String path;
  private String placeholderName;

  private RequestType(String url, String placeholderName) {
      this.url = url;
      this.placeholderName = placeholderName;
      
      this.path = url.replaceAll("/\\{.*\\}", "");
      System.out.println("url: " + url);
      System.out.println("path: " + path);
    }
}
