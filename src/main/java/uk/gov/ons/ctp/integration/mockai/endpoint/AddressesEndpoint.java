package uk.gov.ons.ctp.integration.mockai.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import uk.gov.ons.ctp.integration.mockai.data.DataManager;
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

  private DataManager dataManager = new DataManager();

  @RequestMapping(value = "/addresses/rh/postcode/{postcode}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesRhPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_RH_POSTCODE;
    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

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
    log.info("Request {}", requestType.getPath() + "?input=" + input);

    ResponseEntity<Object> response =
        simulateAIResponse(
            requestType, input, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());

    return response;
  }

  @RequestMapping(value = "/addresses/postcode/{postcode}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_POSTCODE;
    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

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

    ResponseEntity<Object> response = simulateAIResponse(requestType, uprn, 0, 1);

    return response;
  }

  @RequestMapping(value = "/addresses/eq", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesEq(@RequestParam(required = true) String input)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_EQ;
    log.info("Request {}", requestType.getUrl() + "?input=" + input);

    ResponseEntity<Object> response = simulateAIResponse(requestType, input, 0, 10);

    return response;
  }

  @SuppressWarnings("unchecked")
  private ResponseEntity<Object> simulateAIResponse(
      RequestType requestType, String name, int offset, int limit)
      throws IOException, CTPException {

    String baseFileName = dataManager.normaliseFileName(name);

    HttpStatus responseStatus = HttpStatus.OK;
    Object response = null;
    String responseText = dataManager.readCapturedAiResponse(requestType, baseFileName);

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
      responseStatus = requestType.getNotFoundHttpStatus();
      responseText = dataManager.readCapturedAiResponse(requestType, Constants.NO_DATA_FILE_NAME);

      // Customise the not-found response by replacing any place holders with actual values
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
}
