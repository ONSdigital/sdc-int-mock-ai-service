package uk.gov.ons.ctp.integration.mockai.endpoint;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.integration.mockai.data.DataManager;

/**
 * This class holds the /help endpoint to provide a list of supported endpoints and the available
 * data.
 */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class HelpEndpoint implements CTPEndpoint {

  @RequestMapping(value = "/help", method = RequestMethod.GET)
  public ResponseEntity<String> help() throws IOException {

    StringBuilder helpText = new StringBuilder();

    helpText.append("MOCK AI\n");
    helpText.append("  This mock records and replays data from the Address Index service (AI).\n");

    helpText.append("\n\n");
    helpText.append("MOCK ENDPOINTS\n");
    helpText.append("  The following endpoints mock a subset of the AI endpoints.\n");
    helpText.append("  If mock-ai holds data for a request then the response replies with a\n");
    helpText.append("  previously captured AI response. The mock response should be identical \n");
    helpText.append("  to the genuine AI.\n");
    helpText.append("  Endpoints which support offset and limit query parameters return a\n");
    helpText.append("  subset of the data, although it should be noted that the mock holds only\n");
    helpText.append("  the first 1000 or so results from AI.\n");
    for (RequestType requestType : RequestType.values()) {
      helpText.append("\n");
      describeUrl(helpText, requestType, "");
      describeQueryParams(helpText, requestType);
    }

    helpText.append("\n\n");
    helpText.append("CAPTURE ENDPOINTS\n");
    helpText.append("  These allow the dataset used by mock-ai to be extended at run time.\n");
    helpText.append("  If an endpoint URL is prefixed with '/capture' then the request is\n");
    helpText.append("  initially sent to AI. The AI response is then written to file, so\n");
    helpText.append("  that a subsequent request to the corresponding endpoint will respond\n");
    helpText.append("  with the newly capture data.\n");
    for (RequestType requestType : RequestType.values()) {
      helpText.append("\n");
      describeUrl(helpText, requestType, "/capture");
      describeQueryParams(helpText, requestType);
    }

    helpText.append("\n\n");
    helpText.append("EXAMPLE COMMANDS\n");
    helpText.append("\n");
    helpText.append("  Here are some example invocations of the mock-ai\n");
    helpText.append("    $ curl -s localhost:8162/addresses/partial?input=Shirley\n");
    helpText.append(
        "    $ curl -s localhost:8162/addresses/partial?input=Shirley?offset=625;limit=85\n");
    helpText.append("    $ \n");
    helpText.append(
        "    $ curl -s localhost:8162/capture/addresses/partial?input=Rose%20Cottage\n");
    helpText.append("    $ curl -s localhost:8162/addresses/partial?input=Rose%20Cottage\n");

    helpText.append("\n\n");
    helpText.append("DATA HELD\n");
    helpText.append("  The mock endpoints currently hold the following data:\n");
    for (RequestType requestType : RequestType.values()) {
      helpText.append("\n");
      helpText.append("  " + requestType.getUrl() + "\n");

      // Get list of data for this request
      DataManager dataManager = new DataManager();
      List<String> dataFiles = dataManager.listCapturedData(requestType);

      // Load optional property file, for descriptions on the data held
      Properties props = dataManager.getInventory(requestType);

      // List data items held
      for (String name : dataFiles) {
        String normalisedName = dataManager.normaliseFileName(name);
        String dataDescription = props.getProperty(normalisedName, null);
        String dataText = describeData(normalisedName, dataDescription);
        helpText.append("    " + dataText + "\n");
      }
    }

    return ResponseEntity.ok(helpText.toString());
  }

  private void describeUrl(StringBuilder helpText, RequestType requestType, String urlPrefix) {
    helpText.append("  " + urlPrefix + requestType.getUrl() + "\n");
    helpText.append("      " + requestType.getDescription() + "\n");
  }

  private void describeQueryParams(StringBuilder helpText, RequestType requestType) {
    String[] queryParams = requestType.getQueryParams();
    if (queryParams.length > 0) {
      helpText.append("      Query parameters:\n");
      for (String queryParam : queryParams) {
        helpText.append("        - " + queryParam + "\n");
      }
    }
  }

  private String describeData(String normalisedName, String dataDescription) {
    if (dataDescription == null) {
      return normalisedName;
    }

    return String.format("%-16s(%s)", normalisedName, dataDescription);
  }
}
