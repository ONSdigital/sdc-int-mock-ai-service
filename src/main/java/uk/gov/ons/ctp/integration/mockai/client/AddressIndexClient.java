package uk.gov.ons.ctp.integration.mockai.client;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.integration.mockai.endpoint.RequestType;

/**
 * This class is responsible for talking to Address Index.
 *
 * <p>It's used to capture AI responses that can be used for AI simulation.
 */
public class AddressIndexClient {
  private static final Logger log = LoggerFactory.getLogger(AddressIndexClient.class);

  private RestClient restClient;
  private String aiToken;

  public AddressIndexClient(RestClient restClient, String aiToken) throws CTPException {
    this.restClient = restClient;
    this.aiToken = aiToken.trim();

    // Fail if the AI security token has not been set
    if (this.aiToken.isEmpty()) {
      log.with("TokenName", "AI_TOKEN").error("Address Index token not set. Unable to contact AI.");
      throw new CTPException(Fault.RESOURCE_NOT_FOUND, "AI token not set: " + "AI_TOKEN");
    }
  }

  /** Get AI address data by postcode. RH version. */
  public String getAddressesRhPostcode(String postcode) {
    return invokeAI(RequestType.AI_RH_POSTCODE.getUrl(), null, postcode);
  }

  public String getAddressesPartial(String input) {

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
    queryParams.add("input", input);

    String response = invokeAI(RequestType.AI_PARTIAL.getUrl(), queryParams, (String) null);
    return response;
  }

  public String getAddressesPostcode(String postcode) {
    return invokeAI(RequestType.AI_POSTCODE.getUrl(), null, postcode);
  }

  public String getAddressesRhUprn(String uprn) {
    return invokeAI(RequestType.AI_RH_UPRN.getUrl(), null, uprn);
  }

  private String invokeAI(
      String path, MultiValueMap<String, String> queryParams, String... pathParams) {

    if (queryParams == null) {
      queryParams = new LinkedMultiValueMap<String, String>();
    }

    Map<String, String> headerParams = new HashMap<String, String>();
    headerParams.put("Authorization: ", "Bearer " + aiToken);

    String response =
        restClient.getResource(
            path, String.class, headerParams, queryParams, (Object[]) pathParams);

    return response;
  }
}
