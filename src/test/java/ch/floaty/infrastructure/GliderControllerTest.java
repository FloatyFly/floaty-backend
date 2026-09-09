package ch.floaty.infrastructure;

import ch.floaty.domain.model.CertificationClass;
import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.Gradation;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.GliderDetails;
import ch.floaty.domain.service.IGliderApplicationService;
import ch.floaty.run.FloatyApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Covers the glider size/certification fields, and in particular pins the PUT replacement
 * semantics: an absent field clears the stored value rather than preserving it.
 */
@SpringBootTest(classes = FloatyApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GliderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IGliderApplicationService gliderApplicationService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("testuser");
        user.setEmail("test@example.com");

        // The controller reads the principal straight off the SecurityContext.
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Glider gliderWith(String size, CertificationClass certificationClass, Gradation gradation) {
        Glider glider = new Glider();
        glider.setId(42L);
        glider.setUser(user);
        glider.setManufacturer("Skywalk");
        glider.setModel("Cumeo2");
        glider.setSize(size);
        glider.setCertificationClass(certificationClass);
        glider.setGradation(gradation);
        return glider;
    }

    @Test
    @WithMockUser
    public void createGliderWithAllFieldsReturnsThem() throws Exception {
        when(gliderApplicationService.createGlider(any(), any()))
                .thenReturn(gliderWith("95", CertificationClass.B, Gradation.HIGH));

        mockMvc.perform(post("/gliders").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "manufacturer", "Skywalk",
                                "model", "Cumeo2",
                                "size", "95",
                                "certificationClass", "B",
                                "gradation", "HIGH"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.size").value("95"))
                .andExpect(jsonPath("$.certificationClass").value("B"))
                .andExpect(jsonPath("$.gradation").value("HIGH"));

        ArgumentCaptor<GliderDetails> captor = ArgumentCaptor.forClass(GliderDetails.class);
        verify(gliderApplicationService).createGlider(any(), captor.capture());
        assertThat(captor.getValue().getSize()).isEqualTo("95");
        assertThat(captor.getValue().getCertificationClass()).isEqualTo(CertificationClass.B);
        assertThat(captor.getValue().getGradation()).isEqualTo(Gradation.HIGH);
    }

    @Test
    @WithMockUser
    public void createGliderWithoutOptionalFieldsLeavesThemNull() throws Exception {
        when(gliderApplicationService.createGlider(any(), any()))
                .thenReturn(gliderWith(null, null, null));

        mockMvc.perform(post("/gliders").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "manufacturer", "Skywalk",
                                "model", "Cumeo2"))))
                .andExpect(status().isCreated());

        ArgumentCaptor<GliderDetails> captor = ArgumentCaptor.forClass(GliderDetails.class);
        verify(gliderApplicationService).createGlider(any(), captor.capture());
        // Null, not NONE: "not recorded" and "uncertified" are different facts.
        assertThat(captor.getValue().getSize()).isNull();
        assertThat(captor.getValue().getCertificationClass()).isNull();
        assertThat(captor.getValue().getGradation()).isNull();
    }

    @Test
    @WithMockUser
    public void updateOmittingAFieldClearsIt() throws Exception {
        when(gliderApplicationService.findGliderById(42L))
                .thenReturn(gliderWith("95", CertificationClass.B, Gradation.HIGH));
        when(gliderApplicationService.updateGlider(eq(42L), any()))
                .thenReturn(gliderWith(null, null, null));

        // Body omits size, certificationClass and gradation entirely.
        mockMvc.perform(put("/gliders/42").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "manufacturer", "Skywalk",
                                "model", "Cumeo2"))))
                .andExpect(status().isOk());

        ArgumentCaptor<GliderDetails> captor = ArgumentCaptor.forClass(GliderDetails.class);
        verify(gliderApplicationService).updateGlider(eq(42L), captor.capture());
        // Replacement, not merge: the previously-set values must not survive.
        assertThat(captor.getValue().getSize()).isNull();
        assertThat(captor.getValue().getCertificationClass()).isNull();
        assertThat(captor.getValue().getGradation()).isNull();
    }

    @Test
    @WithMockUser
    public void gradationWithoutCertificationClassIsRejected() throws Exception {
        mockMvc.perform(post("/gliders").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "manufacturer", "Skywalk",
                                "model", "Cumeo2",
                                "gradation", "HIGH"))))
                .andExpect(status().isBadRequest());
    }
}
