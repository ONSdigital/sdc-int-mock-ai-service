package uk.gov.ons.ctp.integration.mockai.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;
import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class CaptureAddressesEndpoint implements CTPEndpoint {
  @Autowired private AddressIndexClient addressIndexClient;

  @RequestMapping(value = "/capture/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok(
        "      << MOCK AI >>\n"
            + "Supported endpoints for data capture:\n"
            + "  /capture/"
            + RequestType.AI_RH_POSTCODE.getUrl()
            + "\n"
            + "  /capture/"
            + RequestType.AI_PARTIAL.getUrl()
            + "\n"
            + "  /capture/"
            + RequestType.AI_POSTCODE.getUrl()
            + "\n"
            + "  /capture/"
            + RequestType.AI_RH_UPRN.getUrl()
            + "\n");
  }

  @RequestMapping(value = "/capture/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public Object getAddressesRhPostcode(
      @PathVariable(value = "postcode") String rawPostcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws CTPException, IOException {

    RequestType requestType = RequestType.AI_RH_POSTCODE;
    String postcode = rawPostcode.replaceAll(" ", "").trim();
    log.info("Request {}/{}", requestType.getPath(), v("postcode", rawPostcode));

    // Hit AI and save results
    Object result = addressIndexClient.getAddressesRhPostcode(postcode);
    saveAiResponse(requestType, postcode, result);

    return result;
  }

  @RequestMapping(value = "/capture/addresses/partial", method = RequestMethod.GET)
  public Object getAddressesPartial(@RequestParam(required = true) String input)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_PARTIAL;
    log.info("Request {}", requestType.getUrl(), kv("input", input));

    // Hit AI and save results
    Object result = addressIndexClient.getAddressesPartial(input);
    String cleanedInput = input.replaceAll(" ", "-").trim(); // For sensible file name
    saveAiResponse(requestType, cleanedInput, result);

    return result;
  }

  @RequestMapping(value = "/capture/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public Object getAddressesPostcode(@PathVariable(value = "postcode") String rawPostcode)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_POSTCODE;
    String postcode = rawPostcode.replaceAll(" ", "").trim();
    log.info("Request {}/{}", requestType.getPath(), v("postcode", rawPostcode));

    // Hit AI and save results
    Object result = addressIndexClient.getAddressesPostcode(postcode);
    saveAiResponse(requestType, postcode, result);

    return result;
  }

  @RequestMapping(value = "/capture/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public Object getRhPostcode(@PathVariable(value = "uprn") String uprn)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_RH_UPRN;
    uprn = uprn.trim();
    log.info("Request {}/{}", requestType.getPath(), v("uprn", uprn));

    // Hit AI and save results
    Object result = addressIndexClient.getAddressesRhUprn(uprn);
    saveAiResponse(requestType, uprn, result);

    return result;
  }

  private void saveAiResponse(RequestType requestType, String baseFileName, Object capturedResponse)
      throws IOException, CTPException {
    // Discover the location of the 'data' resource directory
    ClassLoader classLoader = getClass().getClassLoader();
    URL targetDataUrl = classLoader.getResource("data");
    File targetDataDir = new File(targetDataUrl.getFile());

    // Output json data to file in the compiled target file hierarchy (so that it can be immediately
    // used)
    File targetCaptureDir = new File(targetDataDir, requestType.getPath());
    String outputFileName = baseFileName + ".json";
    writeJsonFile(targetCaptureDir, outputFileName, capturedResponse, false);

    // Output json data to file in the source tree (for long term storage)
    File mockAiDir = targetDataDir.getParentFile().getParentFile().getParentFile();
    File srcDataDir = new File(mockAiDir, "src/main/resources/data");
    File srcCaptureDir = new File(srcDataDir, requestType.getPath());
    writeJsonFile(srcCaptureDir, outputFileName, capturedResponse, true);
  }

  private void writeJsonFile(
      File dir, String fileName, Object capturedResponse, boolean failIfNoTargetDir)
      throws CTPException, IOException {
    // Validate target directory
    if (!dir.exists()) {
      if (failIfNoTargetDir) {
        log.error("Output data directory does not exist", kv("output dir", dir.getAbsolutePath()));
        throw new CTPException(
            Fault.SYSTEM_ERROR, "Output data directory does not exist: " + dir.getAbsolutePath());
      } else {
        log.warn("Output data directory does not exist", kv("output dir", dir.getAbsolutePath()));
        return;
      }
    }

    // Convert object to json
    String json = new ObjectMapper().writeValueAsString(capturedResponse);

    // Write AI response json to file
    File outputFile = new File(dir, fileName);
    log.info("Saving AI data to json file", kv("outputFile", outputFile.getAbsoluteFile()));
    Files.write(outputFile.toPath(), json.getBytes());
  }
}
