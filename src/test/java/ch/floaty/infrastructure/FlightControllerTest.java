package ch.floaty.infrastructure;

import ch.floaty.domain.service.AuthenticationService;
import ch.floaty.domain.service.FlightApplicationService;
import ch.floaty.generated.FlightDto;
import ch.floaty.run.FloatyApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FloatyApplication.class)
@AutoConfigureMockMvc
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightApplicationService flightApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testCreateFlight() throws Exception {
        // Arrange
        FlightDto flightDto = new FlightDto();
        flightDto.setFlightId("12345");
        flightDto.setDateTime("2023-10-01T10:00:00");
        flightDto.setTakeOff("Zurich");
        flightDto.setDuration(120L);
        flightDto.setDescription("Test flight");

        // Act + Assert
        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(flightDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/flights/12345"))
                .andExpect(jsonPath("$.flightId").value("12345"))

        // Assert

    }
}
