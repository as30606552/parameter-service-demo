package parameter_service_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A response to a failed HTTP request body validation. Returned on 400 error.")
@SuppressWarnings("unused")
public class ValidationErrorDto {

    @Schema(description = "Time of the error in UTC.", example = "2021-04-19T12:32:56")
    public LocalDateTime timestamp;

    @Schema(description = "HTTP response code.", example = "400")
    public Integer status;

    @Schema(description = "Description of the HTTP response code.", example = "Bad Request")
    public String error;

    @Schema(description = "Relative path to the failed requested operation.", example = "/parameter/1")
    public String path;

    @Schema(description = "Description of the error.", example = "Parameter with id 1 does not exist")
    public String message;

    @Schema(description = "Map of field paths to related error details.", example = "Parameter with id 1 does not exist")
    public Map<String, List<String>> fieldErrors;
}
