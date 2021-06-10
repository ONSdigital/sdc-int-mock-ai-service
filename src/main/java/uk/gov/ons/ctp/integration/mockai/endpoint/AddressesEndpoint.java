package uk.gov.ons.ctp.integration.mockai.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
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
      throws IOException {
    RequestType requestType = RequestType.AI_RH_POSTCODE;

    log.with("postcode", postcode).info("Request " + requestType.getPath() + "/" + postcode);

    postcode = postcode.replaceAll(" ", "").trim();
    String response = simulateAIResponse(requestType, postcode);

    return response;
  }

  @RequestMapping(value = "/addresses/partial", method = RequestMethod.GET)
  public String getAddressesPartial(@RequestParam(required = true) String input)
      throws IOException {
    RequestType requestType = RequestType.AI_PARTIAL;

    log.with("input", input).info("Request " + requestType.getPath());

    String cleanedInput = input.replaceAll(" ", "-").trim();
    String response = simulateAIResponse(requestType, cleanedInput);

    return response;
  }

  @RequestMapping(value = "/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesPostcode(@PathVariable(value = "postcode") String postcode)
      throws IOException {
    RequestType requestType = RequestType.AI_POSTCODE;

    log.with("postcode", postcode).info("Request " + requestType.getPath() + "/" + postcode);

    postcode = postcode.replaceAll(" ", "").trim();
    String response = simulateAIResponse(requestType, postcode);

    return response;
  }

  @RequestMapping(value = "/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public String getRhPostcode(@PathVariable(value = "uprn") String uprn) throws IOException {
    RequestType requestType = RequestType.AI_RH_UPRN;

    log.with("uprn", uprn).info("Request " + requestType.getPath() + "/" + uprn);

    uprn = uprn.replaceAll(" ", "").trim();
    String response = simulateAIResponse(requestType, uprn);

    return response;
  }

  private String simulateAIResponse(RequestType requestType, String baseFileName)
      throws IOException {
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
      throws IOException {
    // Discover the location of the 'data' directory
    ClassLoader classLoader = getClass().getClassLoader();
    URL targetDataUrl = classLoader.getResource("data");
    File targetDataDir = new File(targetDataUrl.getFile());

    // Build expected file name
    File targetCaptureDir = new File(targetDataDir, requestType.getPath());
    String capturedFileName = baseFileName + ".json";
    File capturedFile = new File(targetCaptureDir, capturedFileName);

    if (!capturedFile.exists()) {
      log.with("baseFileName", baseFileName)
          .with("fileName", capturedFile.getAbsoluteFile())
          .info("No captured response");
      return null;
    }

    // Read captured AI response
    String response = Files.readString(capturedFile.toPath());

    return response;
  }
}
