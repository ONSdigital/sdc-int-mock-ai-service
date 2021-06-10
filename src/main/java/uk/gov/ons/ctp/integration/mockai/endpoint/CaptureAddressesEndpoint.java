package uk.gov.ons.ctp.integration.mockai.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.integration.mockai.client.AddressIndexClient;
import uk.gov.ons.ctp.integration.mockai.model.AddressesRhPostcodeRequestDTO;

/**
 * This set of endpoints capture AI responses for a subset of the /addresses endpoints.
 *
 * <p>The set of supported endpoints matches those in AddressesEndpoints, except that these are all
 * prefixed with '/capture'.
 */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class CaptureAddressesEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaptureAddressesEndpoint.class);

  @Autowired private AddressIndexClient addressIndexClient;

  @RequestMapping(value = "/capture/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok(
        "      << MOCK AI >>\n"
            + "Supported endpoints for data capture:\n"
            + "  /capture/" + RequestType.AI_RH_POSTCODE.getUrl() + "\n"
            + "  /capture/" + RequestType.AI_PARTIAL.getUrl() + "\n"
            + "  /capture/" + RequestType.AI_POSTCODE.getUrl() + "\n"
            + "  /capture/" + RequestType.AI_RH_UPRN.getUrl() + "\n");
  }

  @RequestMapping(value = "/capture/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesRhPostcode(
      @PathVariable(value = "postcode") String rawPostcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws CTPException, IOException {
      
    RequestType requestType = RequestType.AI_RH_POSTCODE;
    String postcode = rawPostcode.replaceAll(" ", "").trim();
    log.with("postcode", rawPostcode).info("Request " + requestType.getPath() + "/" + postcode);

    // Hit AI and save results
    String result = addressIndexClient.getAddressesRhPostcode(postcode);
    saveAiResponse(requestType, postcode, result);

    return result;
  }

  @RequestMapping(value = "/capture/addresses/partial", method = RequestMethod.GET)
  public String getAddressesPartial(@RequestParam(required = true) String input)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_PARTIAL;
    log.with("input", input).info("Request " + requestType.getUrl());

    // Hit AI and save results
    String result = addressIndexClient.getAddressesPartial(input);
    String cleanedInput = input.replaceAll(" ", "-").trim();  // For sensible file name
    saveAiResponse(requestType, cleanedInput, result);

    return result;
  }

  @RequestMapping(value = "/capture/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesPostcode(@PathVariable(value = "postcode") String rawPostcode)
      throws IOException, CTPException {
 
    RequestType requestType = RequestType.AI_POSTCODE;
    String postcode = rawPostcode.replaceAll(" ", "").trim();
    log.with("postcode", postcode).info("Request " + requestType.getPath() + "/" + rawPostcode);

    // Hit AI and save results
    String result = addressIndexClient.getAddressesPostcode(postcode);
    saveAiResponse(requestType, postcode, result);

    return result;
  }

  
  @RequestMapping(value = "/capture/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public String getRhPostcode(@PathVariable(value = "uprn") String uprn)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_RH_UPRN;
    uprn = uprn.trim();
    log.with("uprn", uprn).info("Request " + requestType.getPath() + "/" + uprn);

    // Hit AI and save results
    String result = addressIndexClient.getAddressesRhUprn(uprn);
    saveAiResponse(requestType, uprn, result);

    return result;
  }

  
  private void saveAiResponse(RequestType requestType, String baseFileName, String json)
      throws IOException, CTPException {
    // Discover the location of the 'data' resource directory
    ClassLoader classLoader = getClass().getClassLoader();
    URL targetDataUrl = classLoader.getResource("data");
    File targetDataDir = new File(targetDataUrl.getFile());

    // Output json data to file in the compiled target file hierarchy (so that it can be immediately used)
    File targetCaptureDir = new File(targetDataDir, requestType.getPath());
    String outputFileName = baseFileName + ".json";
    writeJsonFile(targetCaptureDir, outputFileName, json, false);

    // Output json data to file in the source tree (for long term storage)
    File mockAiDir = targetDataDir.getParentFile().getParentFile().getParentFile();
    File srcDataDir = new File(mockAiDir, "src/main/resources/data");
    File srcCaptureDir = new File(srcDataDir, requestType.getPath());
    writeJsonFile(srcCaptureDir, outputFileName, json, true);
  }

  
  private void writeJsonFile(File dir, String fileName, String json, boolean failIfNoTargetDir)
      throws CTPException, IOException {
    if (!dir.exists()) {
      if (failIfNoTargetDir) {
        log.with("output dir", dir.getAbsolutePath()).error("Output data directory does not exist");
        throw new CTPException(
            Fault.SYSTEM_ERROR, "Output data directory does not exist: " + dir.getAbsolutePath());
      } else {
        log.with("output dir", dir.getAbsolutePath()).warn("Output data directory does not exist");
        return;
      }
    }

    // Write AI response json to file
    File outputFile = new File(dir, fileName);
    log.with("outputFile", outputFile.getAbsoluteFile()).info("Saving AI data to json file");
    Files.write(outputFile.toPath(), json.getBytes());
  }
}
