package uk.gov.ons.ctp.integration.mockcaseapiservice.endpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mockcaseapiservice.client.AddressIndexClient;
import uk.gov.ons.ctp.integration.mockcaseapiservice.model.AddressesRhPostcodeRequestDTO;

/** Provides mock endpoints for the case service. */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class CaptureAddressesEndpoints implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaptureAddressesEndpoints.class);

  @Autowired private AddressIndexClient addressIndexClient;


  @RequestMapping(value = "/capture/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok("      << MOCK AI >>\n"
    		+ "Supported endpoints:\n"
    		+ "  /capture/addresses/rh/postcode/{postcode} \n"
        + "  /capture/addresses/partial \n"
        + "  /capture/addresses/postcode/{postcode} \n"
        + "  /capture/addresses/rh/uprn \n");
  }

  @RequestMapping(value = "/capture/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesRhPostcode(@PathVariable(value = "postcode") String rawPostcode, @Valid AddressesRhPostcodeRequestDTO requestParamsDTO) throws CTPException, IOException {
    log.with("postcode", rawPostcode).info("Request /capture/addresses/rh/postcode/" + rawPostcode);

    String postcode = rawPostcode.replaceAll(" ", "").trim();
    
    String result = addressIndexClient.getAddressesRhPostcode(postcode);
    saveResult(RequestType.AI_RH_POSTCODE, postcode, result);
    
	  return result;
  }

  private void saveResult(RequestType requestType, String postcode, String result) throws IOException {

    ClassLoader classLoader = getClass().getClassLoader();

    URL x = classLoader.getResource("data");
    File topLevelDir = new File(x.getFile());
    
    File captureDir = new File(topLevelDir, requestType.getPath());
    String fileName = postcode + ".json";
    File captureFile = new File(captureDir, fileName);
    
    System.out.println(captureFile.getAbsolutePath());
    
    Files.write(captureFile.toPath(), result.getBytes());
  }

  
  
  @RequestMapping(value = "/capture/addresses/partial", method = RequestMethod.GET)
  public String getAddressesPartial(@RequestParam(required = true) String input) {
    log.with("input", input).info("Request /capture/addresses/partial");

    String result = addressIndexClient.getAddressesPartial(input);
    
    return result;
  }

  @RequestMapping(value = "/capture/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesPostcode(@PathVariable(value = "postcode") String postcode) {
    log.with("postcode", postcode).info("Request /capture/addresses/postcode/{postcode}");

    String result = addressIndexClient.getAddressesPostcode(postcode);
    
    return result;
  }

  @RequestMapping(value = "/capture/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public String getRhPostcode(@PathVariable(value = "uprn") String uprn) {
    log.with("uprn", uprn).info("Request /capture/addresses/rh/uprn/{uprn}");

    String result = addressIndexClient.getAddressesRhUprn(uprn);
    
    return result;
  }

  private void readDataFile() {
    String fileName = "data/addresses/rh/postcode/x.txt";

    ClassLoader classLoader = getClass().getClassLoader();

    try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader)) {

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
