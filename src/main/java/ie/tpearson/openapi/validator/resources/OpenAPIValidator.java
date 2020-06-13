package ie.tpearson.openapi.validator.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.schema.validator.ValidationData;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.openapi4j.core.validation.ValidationSeverity.WARNING;

/**
 * Wrapper to simplify OpenAPI validation tools
 */
public class OpenAPIValidator {

  private static final ClassLoader classLoader = OpenAPIValidator.class.getClassLoader();

  private final OpenApi3 spec;

  private OpenAPIValidator(OpenApi3 spec) {
    this.spec = spec;
  }

  /**
   * @param pathToSpecFile Relative location of your OpenAPI specification in the resources folder
   * @return A ready-to-go OPEN API validator
   */
  public static OpenAPIValidator from(String pathToSpecFile) {
    URL resource = classLoader.getResource(pathToSpecFile);
    Objects.requireNonNull(resource);
    String specFilePath = resource.getFile();
    File specFile = new File(specFilePath);
    final OpenApi3Parser parser = new OpenApi3Parser();
    final OpenApi3 spec;
    try {
      spec = parser.parse(specFile, true);
      return new OpenAPIValidator(spec);
    } catch (ResolutionException e) {
      throw new IllegalArgumentException("Could not find OpenAPI specification file at " + specFilePath, e);
    } catch (ValidationException e) {
      String message = "The OpenAPI specification file at " + pathToSpecFile + " is not valid: " + e.getMessage();
      throw new IllegalArgumentException(message, e);
    }
  }

  private OperationValidator operationValidator(String path, String operationId) {
    final Path specPath = spec.getPath(path);
    final Operation operation = spec.getOperationById(operationId);
    return new OperationValidator(spec, specPath, operation);
  }


  /**
   * Validates the given response against the specification file in this validator.
   *
   * @param path               Path of the input operation to validate
   * @param operationId        OperationId of the input in the OpenAPI spec file.
   * @param responseStatusCode Status code for the HTTP response
   * @param responseObject     The response to validate
   * @throws IllegalArgumentException if there are any validation issues at or above WARNING severity
   */
  public void validateResponse(String path, String operationId, int responseStatusCode, Object responseObject) {
    String jsonResponseBody = serialize(responseObject);

    //build objects to validate
    OperationValidator validator = operationValidator(path, operationId);
    DefaultResponse.Builder bodyBuilder = new DefaultResponse.Builder(responseStatusCode);
    bodyBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    DefaultResponse body = bodyBuilder.body(Body.from(jsonResponseBody)).build();

    //Validate
    ValidationData<Object> resultHolder = new ValidationData<>();
    validator.validateBody(body, resultHolder);
    final ValidationResults results = resultHolder.results();

    //Throw exceptions from the result
    if (results.severity().ge(WARNING)) {
      String failures = results.items().stream().filter(r -> r.severity().ge(WARNING)).map(String::valueOf).collect(Collectors.joining("\n"));
      throw new IllegalArgumentException("Response body " + jsonResponseBody + " does not match schema:\n" + failures);
    }
  }

  private String serialize(Object responseObject) {
    try {
      return new ObjectMapper().writeValueAsString(responseObject);
    } catch (JsonProcessingException jpe) {
      throw new IllegalArgumentException(jpe);
    }
  }


}
