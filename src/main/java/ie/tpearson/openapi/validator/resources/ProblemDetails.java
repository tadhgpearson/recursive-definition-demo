package ie.tpearson.openapi.validator.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {

  private String type;
  private String title;
  private Integer status;
  private String detail;
  private String instance;
  private ProblemDetails childError;

}
