package parameter_service_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A parameter of calculation.")
public class ParameterDto {

    @Schema(description = "The unique identifier the parameter.")
    private Long id;

    @Schema(description = "The name of the parameter.")
    private String name;

    @Schema(description = "The value of the parameter.")
    private String value;

    @Schema(description = "Is the parameter activated or not.")
    private ParameterStatus status;

    @Nullable
    @Schema(description = "If parameter is activated, this will be the activation date, otherwise null.")
    private LocalDate activeFrom;
}
