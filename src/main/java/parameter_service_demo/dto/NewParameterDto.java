package parameter_service_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A request for creation of a new parameter.")
public class NewParameterDto {

    @NotNull
    @NotEmpty
    @Schema(description = "The name of the parameter.")
    private String name;

    @NotNull
    @NotEmpty
    @Schema(description = "The value of the parameter.")
    private String value;
}
