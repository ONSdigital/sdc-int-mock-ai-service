package uk.gov.ons.ctp.integration.mockcaseapiservice.endpoint;

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

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.integration.mockcaseapiservice.model.AddressesRhPostcodeRequestDTO;

/** Provides mock endpoints for a subset of the AI /addresses endpoints. */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class AddressesEndpoints implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(AddressesEndpoints.class);

  @RequestMapping(value = "/addresses/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok(
        "      << MOCK AI >>\n"
            + "Supported endpoints:\n"
            + "  /addresses/rh/postcode/{postcode} \n"
            + "  /addresses/partial \n"
            + "  /addresses/postcode/{postcode} \n"
            + "  /addresses/rh/uprn \n");
  }

  @RequestMapping(value = "/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesRhPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO) throws IOException {
    log.with("postcode", postcode).info("Request /addresses/rh/postcode/" + postcode);

    postcode = postcode.replaceAll(" ", "").trim();
    String response = readDataFile(RequestType.AI_RH_POSTCODE, postcode);
    
    return response;
  }

  @RequestMapping(value = "/addresses/partial", method = RequestMethod.GET)
  public String getAddressesPartial(@RequestParam(required = true) String input) throws IOException {
    log.with("input", input).info("Request /addresses/partial");

    String cleanedInput = input.replaceAll(" ", "-").trim();
    String response = readDataFile(RequestType.AI_PARTIAL, cleanedInput);
    
    return response;
  }

  @RequestMapping(value = "/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesPostcode(@PathVariable(value = "postcode") String postcode) throws IOException {
      log.with("postcode", postcode).info("Request /addresses/postcode/" + postcode);

      postcode = postcode.replaceAll(" ", "").trim();
      String response = readDataFile(RequestType.AI_POSTCODE, postcode);
      
      return response;
  }

  @RequestMapping(value = "/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public String getRhPostcode(@PathVariable(value = "uprn") String uprn) throws IOException {
      log.with("uprn", uprn).info("Request /addresses/rh/uprn/" + uprn);

      uprn = uprn.replaceAll(" ", "").trim();
      String response = readDataFile(RequestType.AI_RH_UPRN, uprn);
      
      return response;
  }

  private String readDataFile(RequestType requestType, String baseFileName) throws IOException {
    // Discover the location of the 'data' directory
    ClassLoader classLoader = getClass().getClassLoader();
    URL targetDataUrl = classLoader.getResource("data");
    File targetDataDir = new File(targetDataUrl.getFile());

    // Output json data to file in the compiled target file hierarchy
    File targetCaptureDir = new File(targetDataDir, requestType.getPath());
    String capturedFileName = baseFileName + ".json";
    File capturedFile = new File(targetCaptureDir, capturedFileName);
    String response = Files.readString(capturedFile.toPath());
    
    return response;
//    try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
//        InputStreamReader streamReader =
//            new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//        BufferedReader reader = new BufferedReader(streamReader)) {
//
//      String line;
//      while ((line = reader.readLine()) != null) {
//        System.out.println(line);
//      }
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }
}
