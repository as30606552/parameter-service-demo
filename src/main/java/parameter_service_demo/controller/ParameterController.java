package parameter_service_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import parameter_service_demo.dto.ErrorDto;
import parameter_service_demo.dto.NewParameterDto;
import parameter_service_demo.dto.ParameterDto;
import parameter_service_demo.dto.ValidationErrorDto;
import parameter_service_demo.service.ParameterService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("parameter")
@RequiredArgsConstructor
public class ParameterController {

    private final ParameterService parameterService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates a new parameter with provided name and value.")
    @ApiResponse(responseCode = "201", description = "A new successfully created parameter.")
    @ApiResponse(
            responseCode = "400",
            description = "Request contains invalid data, e.g. misses a required field.",
            content = @Content(schema = @Schema(oneOf = {ErrorDto.class, ValidationErrorDto.class}))
    )
    public ResponseEntity<ParameterDto> createParameter(@RequestBody @Valid NewParameterDto newParameterDto) {
        var parameter = parameterService.save(newParameterDto);
        return ResponseEntity.created(
                        UriComponentsBuilder.newInstance()
                                .pathSegment("parameter", parameter.getId().toString())
                                .build()
                                .toUri()
                )
                .body(parameter);
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds a parameter with ID provided in the path.")
    @ApiResponse(responseCode = "200", description = "Parameter.")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid id specified.",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Parameter with specified ID not found.",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    public ParameterDto getParameterById(@PathVariable Long id) {
        return parameterService.loadById(id);
    }

    @PutMapping(value = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a parameter with ID provided in the path.")
    @ApiResponse(responseCode = "200", description = "Updated parameter.")
    @ApiResponse(
            responseCode = "400",
            description = "Request contains invalid data, e.g. misses a required field or invalid ID specified.",
            content = @Content(schema = @Schema(oneOf = {ErrorDto.class, ValidationErrorDto.class}))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Parameter with specified ID not found.",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    public ParameterDto updateParameterById(@PathVariable Long id, @RequestBody @Valid NewParameterDto newParameterDto) {
        return parameterService.updateById(id, newParameterDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Updates a parameter with ID provided in the path.")
    @ApiResponse(responseCode = "204", description = "Parameter deleted successfully.")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid id specified.",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    public void deleteParameterById(@PathVariable Long id) {
        parameterService.deleteById(id);
    }

    @PostMapping("activate/{name}")
    @Operation(summary = "Activates (locks) a parameter with ID provided in the path.")
    @ApiResponse(responseCode = "200", description = "Parameter activated successfully.")
    @ApiResponse(
            responseCode = "400",
            description = "Parameter is already activated.",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    public List<ParameterDto> activateByName(@PathVariable String name, @RequestParam LocalDate activationDate) {
        return parameterService.activateByName(name, activationDate);
    }
}
