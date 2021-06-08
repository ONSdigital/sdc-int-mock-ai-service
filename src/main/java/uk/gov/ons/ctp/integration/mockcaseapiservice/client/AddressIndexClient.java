package uk.gov.ons.ctp.integration.mockcaseapiservice.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.common.rest.RestClient;

public class AddressIndexClient {
  private static final Logger log = LoggerFactory.getLogger(AddressIndexClient.class);

  private RestClient restClient;
  private String aiToken;

  public AddressIndexClient(RestClient restClient, String aiToken) throws CTPException {
    this.restClient = restClient;
    this.aiToken = aiToken.trim();

    if (this.aiToken.isEmpty()) {
      log.with("TokenName", "AI_TOKEN").error("Address Index token not set. Unable to contact AI.");
      throw new CTPException(Fault.RESOURCE_NOT_FOUND, "AI token not set: " + "AI_TOKEN");
    }
  }
  
  /** 
   * Get AI address data by postcode. RH version.
   */
  public String getAddressesRhPostcode(String postcode) {

    String path = "/addresses/rh/postcode/{postcode}";
    String r = invokeAI(path, postcode);
    System.out.println("Result: " + r);
    return r;
  }

  public String getAddressesPartial(String input) {
    String path = "/addresses/partial";

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
    queryParams.add("input", input);
    String r = invokeAI(path, queryParams, (String) null);
    System.out.println("Result: " + r);
    return r;
  }

  public String getAddressesPostcode(String postcode) {
    String path = "/addresses/postcode/{postcode}";
    String r = invokeAI(path, postcode);
    System.out.println("Result: " + r);
    return r;
  }

  public String getAddressesRhUprn(String uprn) {
    String path = "/addresses/rh/uprn/{uprn}";
    String r = invokeAI(path, uprn);
    System.out.println("Result: " + r);
    return r;
  }

  private String invokeAI(String path, String... postcode) {
    Map<String, String> headerParams = new HashMap<String, String>();

    headerParams.put("Authorization: ", "Bearer " + aiToken);
    
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();

    return invokeAI(path, queryParams, postcode);
  }
  
  private String invokeAI(String path, MultiValueMap<String, String> queryParams, String... postcode) {
    Map<String, String> headerParams = new HashMap<String, String>();

    headerParams.put("Authorization: ", "Bearer " + aiToken);
    
    String r = restClient.getResource(path, String.class, headerParams, queryParams, postcode);
    return r;
  }
  
}
