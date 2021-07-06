package uk.gov.ons.ctp.integration.mockai.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public final class HelpEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(HelpEndpoint.class);

  @Autowired private AddressIndexClient addressIndexClient;

  @RequestMapping(value = "/help", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok(
        "      << MOCK AI >>\n"
            + "Help endpoint.\nSupported endpoints for data capture:\n"
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

}
