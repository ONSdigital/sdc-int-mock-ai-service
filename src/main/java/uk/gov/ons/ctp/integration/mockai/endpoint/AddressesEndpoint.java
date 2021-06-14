package uk.gov.ons.ctp.integration.mockai.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mockai.model.AddressesRhPostcodeRequestDTO;

/** Provides mock endpoints for a subset of the AI /addresses endpoints. */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class AddressesEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(AddressesEndpoint.class);

  @RequestMapping(value = "/addresses/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok(
        "      << MOCK AI >>\n"
            + "Supported addresses endpoints:\n"
            + "  "
            + RequestType.AI_RH_POSTCODE.getUrl()
            + "\n"
            + "  "
            + RequestType.AI_PARTIAL.getUrl()
            + "\n"
            + "  "
            + RequestType.AI_POSTCODE.getUrl()
            + "\n"
            + "  "
            + RequestType.AI_RH_UPRN.getUrl()
            + "\n");
  }

  @RequestMapping(value = "/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesRhPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_RH_POSTCODE;

    log.with("postcode", postcode).info("Request " + requestType.getPath() + "/" + postcode);

    postcode = postcode.replaceAll(" ", "").trim();
    String response = simulateAIResponse(requestType, postcode);

    return response;
  }

  @RequestMapping(value = "/addresses/partial", method = RequestMethod.GET)
  public String getAddressesPartial(@RequestParam(required = true) String input)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_PARTIAL;

    log.with("input", input).info("Request " + requestType.getPath());

    String cleanedInput = input.replaceAll(" ", "-").trim();
    String response = simulateAIResponse(requestType, cleanedInput);

    return response;
  }

  @RequestMapping(value = "/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesPostcode(@PathVariable(value = "postcode") String postcode)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_POSTCODE;

    log.with("postcode", postcode).info("Request " + requestType.getPath() + "/" + postcode);

    postcode = postcode.replaceAll(" ", "").trim();
    String response = simulateAIResponse(requestType, postcode);

    return response;
  }

  @RequestMapping(value = "/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public String getRhPostcode(@PathVariable(value = "uprn") String uprn)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_RH_UPRN;

    log.with("uprn", uprn).info("Request " + requestType.getPath() + "/" + uprn);

    uprn = uprn.replaceAll(" ", "").trim();
    String response = simulateAIResponse(requestType, uprn);

    return response;
  }

  private String simulateAIResponse(RequestType requestType, String baseFileName)
      throws IOException, CTPException {
    String response = readCapturedAiResponse(requestType, baseFileName);

    if (response == null) {
      response = readCapturedAiResponse(requestType, "notFound");

      // Replace any place holders with actual values
      String placeholderName = requestType.getPlaceholderName();
      if (placeholderName != null) {
        String fullPlaceholderName = "%" + placeholderName + "%";
        response = response.replace(fullPlaceholderName, baseFileName);
      }
    }

    return response;
  }

  private String readCapturedAiResponse(RequestType requestType, String baseFileName)
      throws IOException, CTPException {
    // Work out where the captured data lives
    ClassLoader classLoader = getClass().getClassLoader();
    String resourceName = "data" + requestType.getPath() + "/" + baseFileName + ".json";

    // Return nothing if data not held for request
    URL resource = classLoader.getResource(resourceName);
    if (resource == null) {
      log.with("baseFileName", baseFileName)
          .with("resource", resourceName)
          .with("requestType", requestType.name())
          .info("No captured response");
      return null;
    }

    // Read AI captured response
    InputStream targetDataUrl = resource.openStream();
    StringBuilder responseBuilder = new StringBuilder();
    try (Reader reader =
        new BufferedReader(
            new InputStreamReader(targetDataUrl, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c = 0;
      while ((c = reader.read()) != -1) {
        responseBuilder.append((char) c);
      }
    }

    return responseBuilder.toString();
  }
}
