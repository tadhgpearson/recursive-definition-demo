package ie.tpearson.openapi.validator.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class APIEndpoint {

  @GET public ProblemDetails endpoint(@QueryParam("generateError") @DefaultValue("false") boolean generateError) {
    int responseStatus = generateError ? 400 : 200;
    return ProblemDetails.builder().status(responseStatus).
      type("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/" + responseStatus).title("Sample response").build();
  }
}
