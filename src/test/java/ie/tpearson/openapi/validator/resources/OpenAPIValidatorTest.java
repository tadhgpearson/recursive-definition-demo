package ie.tpearson.openapi.validator.resources;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class OpenAPIValidatorTest {

  private final OpenAPIValidator underTest = OpenAPIValidator.from("recursive-definition.yaml");

  @Test public void testValidResponse() {
    ProblemDetails response =
      ProblemDetails.builder().status(400).type("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/400").title("Sample error").build();
    underTest.validateResponse("/", "retrieveApplicationCollection", 400, response);
    assertTrue(true);
  }

}
