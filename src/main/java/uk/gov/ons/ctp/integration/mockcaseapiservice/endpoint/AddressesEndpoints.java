package uk.gov.ons.ctp.integration.mockcaseapiservice.endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
import uk.gov.ons.ctp.integration.common.product.model.Product.CaseType;
import uk.gov.ons.ctp.integration.mockcaseapiservice.model.AddressesRhPostcodeRequestDTO;

/** Provides mock endpoints for the case service. */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class AddressesEndpoints implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(AddressesEndpoints.class);


  @RequestMapping(value = "/addresses/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok("      << MOCK AI >>\n"
    		+ "Supported endpoints:\n"
    		+ "  /addresses/rh/postcode/{postcode} \n"
            + "  /addresses/partial \n"
            + "  /addresses/postcode/{postcode} \n"
            + "  /addresses/rh/uprn \n");
  }

  @RequestMapping(value = "/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesRhPostcode(@PathVariable(value = "postcode") String postcode, @Valid AddressesRhPostcodeRequestDTO requestParamsDTO) {
    log.with("postcode", postcode).info("Request /addresses/rh/postcode/" + postcode);

    readDataFile();
    System.out.println("epoch = " + requestParamsDTO.getEpoch());
      
	  return "/addresses/rh/postcode/{postcode}";
  }

  @RequestMapping(value = "/addresses/partial", method = RequestMethod.GET)
  public String getAddressesPartial(@RequestParam(required = true) String input) {
    log.with("input", input).info("Request /addresses/partial");

    return "/addresses/partial";
  }

  @RequestMapping(value = "/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public String getAddressesPostcode(@PathVariable(value = "postcode") String postcode) {
	  return "/addresses/postcode/{postcode}";
  }

  @RequestMapping(value = "/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public String getRhPostcode(@PathVariable(value = "uprn") String uprn) {
	  return "/addresses/rh/uprn/{uprn}";
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
