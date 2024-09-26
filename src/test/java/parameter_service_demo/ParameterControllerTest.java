package parameter_service_demo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import parameter_service_demo.model.ParameterEntity;
import parameter_service_demo.repository.ParameterRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ParameterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParameterRepository parameterRepository;

    @Test
    void shouldCreateParameter() throws Exception {
        // given
        // language=JSON
        var newParameter = """
                {
                  "name": "new_parameter",
                  "value": "1"
                }\
                """;

        // when
        var result = mockMvc.perform(
                post("/parameter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newParameter)
        );

        // then
        result
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("new_parameter"))
                .andExpect(jsonPath("$.value").value("1"));

        var content = result.andReturn().getResponse().getContentAsString();
        var id = (int) JsonPath.read(content, "$.id");
        assertThat(parameterRepository.existsById((long) id)).isTrue();
    }

    @Test
    void shouldReturnBadRequestWhenNoBodyProvidedOnCreateParameter() throws Exception {
        // when
        var result = mockMvc.perform(post("/parameter").contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenBodyMissingRequiredFieldOnCreateParameter() throws Exception {
        // given
        // language=JSON
        var newParameter = """
                {
                  "value": "1"
                }\
                """;
        // when
        var result = mockMvc.perform(
                post("/parameter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newParameter)
        );

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter"))
                .andExpect(jsonPath("$.message").value("Failed to parse request body"))
                .andExpect(jsonPath("$.fieldErrors.name[0]").isNotEmpty());
    }

    @Test
    void shouldGetParameter() throws Exception {
        // given
        var parameter = ParameterEntity.builder()
                .name("test_parameter")
                .value("2")
                .build();
        var id = parameterRepository.save(parameter).getId();

        // when
        var result = mockMvc.perform(get("/parameter/{id}", id));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("test_parameter"))
                .andExpect(jsonPath("$.value").value("2"));
    }

    @Test
    void shouldReturnNotFoundWhenParameterNotFoundOnGetParameter() throws Exception {
        // given
        var id = Long.MAX_VALUE;

        // when
        var result = mockMvc.perform(get("/parameter/{id}", id));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter/" + id))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestOnGetParameterWithInvalidId() throws Exception {
        // when
        var result = mockMvc.perform(get("/parameter/abc"));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter/abc"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldUpdateParameter() throws Exception {
        // given
        var parameter = ParameterEntity.builder()
                .name("original_parameter")
                .value("2")
                .build();
        var id = parameterRepository.save(parameter).getId();
        // language=JSON
        var parameterUpdate = """
                {
                  "name": "updated_parameter",
                  "value": "1"
                }\
                """;

        // when
        var result = mockMvc.perform(
                put("/parameter/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parameterUpdate)
        );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("updated_parameter"))
                .andExpect(jsonPath("$.value").value("1"));
        assertThat(parameterRepository.findById(id))
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("name", "updated_parameter")
                .hasFieldOrPropertyWithValue("value", "1");
    }

    @Test
    void shouldReturnNotFoundWhenParameterNotFoundOnUpdateParameter() throws Exception {
        // given
        // language=JSON
        var id = Long.MAX_VALUE;
        var parameterUpdate = """
                {
                  "name": "updated_parameter",
                  "value": "1"
                }\
                """;

        // when
        var result = mockMvc.perform(
                put("/parameter/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parameterUpdate)
        );

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter/" + id))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestOnUpdateParameterWithInvalidId() throws Exception {
        // given
        // language=JSON
        var parameterUpdate = """
                {
                  "name": "updated_parameter",
                  "value": "1"
                }\
                """;

        // when
        var result = mockMvc.perform(
                put("/parameter/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parameterUpdate)
        );

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter/abc"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenNoBodyProvidedOnUpdateParameter() throws Exception {
        // when
        var result = mockMvc.perform(put("/parameter/1").contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter/1"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenBodyMissingRequiredFieldOnUpdateParameter() throws Exception {
        // given
        // language=JSON
        var newParameter = """
                {
                  "value": "1"
                }\
                """;
        // when
        var result = mockMvc.perform(
                post("/parameter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newParameter)
        );

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter"))
                .andExpect(jsonPath("$.message").value("Failed to parse request body"))
                .andExpect(jsonPath("$.fieldErrors.name[0]").isNotEmpty());
    }

    @Test
    void shouldDeleteParameter() throws Exception {
        // given
        var parameter = ParameterEntity.builder()
                .name("parameter")
                .value("3")
                .build();
        var id = parameterRepository.save(parameter).getId();

        // when
        var result = mockMvc.perform(delete("/parameter/{id}", id));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestOnDeleteParameterWithInvalidId() throws Exception {
        // when
        var result = mockMvc.perform(delete("/parameter/abc"));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/parameter/abc"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
