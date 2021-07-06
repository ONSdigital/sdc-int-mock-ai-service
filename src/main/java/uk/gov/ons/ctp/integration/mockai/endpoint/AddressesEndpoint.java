package uk.gov.ons.ctp.integration.mockai.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;
import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPartialAddressDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexRhPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mockai.addressindex.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mockai.misc.Constants;
import uk.gov.ons.ctp.integration.mockai.model.AddressesPartialRequestDTO;
import uk.gov.ons.ctp.integration.mockai.model.AddressesPostcodeRequestDTO;
import uk.gov.ons.ctp.integration.mockai.model.AddressesRhPostcodeRequestDTO;
import uk.gov.ons.ctp.integration.mockai.model.AddressesRhUprnRequestDTO;

/** Provides mock endpoints for a subset of the AI /addresses endpoints. */
@Slf4j
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class AddressesEndpoint implements CTPEndpoint {

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
  public ResponseEntity<Object> getAddressesRhPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_RH_POSTCODE;

    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

    postcode = postcode.replaceAll(" ", "").trim();
    ResponseEntity<Object> response =
        simulateAIResponse(
            requestType, postcode, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());

    return response;
  }

  @RequestMapping(value = "/addresses/partial", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesPartial(
      @RequestParam(required = true) String input,
      @Valid AddressesPartialRequestDTO requestParamsDTO)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_PARTIAL;

    log.info("Request {}", requestType.getPath(), kv("input", input));

    String cleanedInput = input.replaceAll(" ", "-").trim();
    ResponseEntity<Object> response =
        simulateAIResponse(
            requestType, cleanedInput, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());

    return response;
  }

  @RequestMapping(value = "/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_POSTCODE;

    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

    postcode = postcode.replaceAll(" ", "").trim();
    ResponseEntity<Object> response =
        simulateAIResponse(
            requestType, postcode, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());

    return response;
  }

  @RequestMapping(value = "/addresses/rh/uprn/{uprn}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesRhUprn(
      @PathVariable(value = "uprn") String uprn, @Valid AddressesRhUprnRequestDTO requestParamsDTO)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_RH_UPRN;

    log.info("Request {}/{}", requestType.getPath(), v("uprn", uprn));

    uprn = uprn.replaceAll(" ", "").trim();
    ResponseEntity<Object> response = simulateAIResponse(requestType, uprn, 0, 1);

    return response;
  }
  
  @RequestMapping(value = "/addresses/eq", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesEq(@RequestParam(required = true) String input)
      throws IOException, CTPException {
    RequestType requestType = RequestType.AI_EQ;

    log.with("input", input).info("Request " + requestType.getPath());

    String cleanedInput = input.replaceAll(" ", "-").trim();
    ResponseEntity<Object> response = simulateAIResponse(requestType, cleanedInput, 0, 10);

    return response;
  }

  @SuppressWarnings("unchecked")
  private ResponseEntity<Object> simulateAIResponse(
      RequestType requestType, String baseFileName, int offset, int limit)
      throws IOException, CTPException {
    HttpStatus responseStatus = HttpStatus.OK;
    Object response = null;
    String responseText = readCapturedAiResponse(requestType, baseFileName);

    if (responseText != null) {
      // Convert captured AI response to an object, and return the target subset of data
      response =
          new ObjectMapper().readerFor(requestType.getResponseClass()).readValue(responseText);

      switch (requestType) {
        case AI_RH_POSTCODE:
          AddressIndexRhPostcodeResultsDTO rhPostcodes =
              (AddressIndexRhPostcodeResultsDTO) response;
          List<AddressIndexRhPostcodeAddressDTO> rhPostcodeAddresses =
              (List<AddressIndexRhPostcodeAddressDTO>)
                  subset(rhPostcodes.getResponse().getAddresses(), offset, limit);
          rhPostcodes.getResponse().setAddresses(rhPostcodeAddresses);
          rhPostcodes.getResponse().setOffset(offset);
          rhPostcodes.getResponse().setLimit(limit);
          // Replicate the counting down of the confidence score
          int confidence = 100000 + rhPostcodeAddresses.size();
          for (AddressIndexRhPostcodeAddressDTO address : rhPostcodeAddresses) {
            address.setConfidenceScore(confidence--);
          }
          break;
        case AI_PARTIAL:
          AddressIndexPartialResultsDTO partial = (AddressIndexPartialResultsDTO) response;
          List<AddressIndexPartialAddressDTO> partialAddresses =
              (List<AddressIndexPartialAddressDTO>)
                  subset(partial.getResponse().getAddresses(), offset, limit);
          partial.getResponse().setAddresses(partialAddresses);
          partial.getResponse().setOffset(offset);
          partial.getResponse().setLimit(limit);
          break;
        case AI_POSTCODE:
          AddressIndexPostcodeResultsDTO postcodes = (AddressIndexPostcodeResultsDTO) response;
          List<AddressIndexPostcodeAddressDTO> postcodeAddresses =
              (List<AddressIndexPostcodeAddressDTO>)
                  subset(postcodes.getResponse().getAddresses(), offset, limit);
          postcodes.getResponse().setAddresses(postcodeAddresses);
          postcodes.getResponse().setOffset(offset);
          postcodes.getResponse().setLimit(limit);
          break;
        case AI_EQ:
          // Nothing to do for type-ahead response
          break;
        case AI_RH_UPRN:
          // Nothing to do for uprn results
          break;
        default:
          throw new CTPException(
              Fault.SYSTEM_ERROR, "Unrecognised request type: " + requestType.name());
      }
    } else {
      // 404 - not found
      responseStatus = HttpStatus.NOT_FOUND;
      responseText = readCapturedAiResponse(requestType, Constants.NO_DATA_FILE_NAME);

      // Replace any place holders with actual values
      String placeholderName = requestType.getPlaceholderName();
      if (placeholderName != null) {
        String fullPlaceholderName = "%" + placeholderName + "%";
        responseText = responseText.replace(fullPlaceholderName, baseFileName);
      }

      response = responseText;
    }

    return new ResponseEntity<Object>(response, responseStatus);
  }

  private List<?> subset(List<?> addresses, int offset, int limit) {
    if (offset > addresses.size() || offset < 0) {
      return new ArrayList<Object>();
    }

    int toIndex = Math.min(offset + limit, addresses.size());

    return addresses.subList(offset, toIndex);
  }

  private String readCapturedAiResponse(RequestType requestType, String baseFileName)
      throws IOException, CTPException {
    // Work out where the captured data lives
    ClassLoader classLoader = getClass().getClassLoader();
    String resourceName = "data" + requestType.getPath() + "/" + baseFileName + ".json";

    // Return nothing if data not held for request
    URL resource = classLoader.getResource(resourceName);
    if (resource == null) {
      log.info(
          "No captured response",
          kv("baseFileName", baseFileName),
          kv("resource", resourceName),
          kv("requestType", requestType.name()));
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
