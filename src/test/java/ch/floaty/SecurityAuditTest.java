package ch.floaty;

import ch.floaty.domain.model.Flight;
import ch.floaty.domain.model.FlightParameters;
import ch.floaty.domain.model.IgcData;
import ch.floaty.domain.model.IgcMetadata;
import ch.floaty.domain.model.User;
import ch.floaty.domain.repository.IFlightRepository;
import ch.floaty.domain.service.IFlightApplicationService;
import ch.floaty.run.FloatyApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exploit tests for the findings in {@code docs/security/2026-09-audit.md}.
 *
 * <p>Every test here is {@link Disabled} on purpose. Each one describes the behaviour Floaty
 * *should* have once the corresponding Finding is fixed, so each currently fails. A fix ticket
 * removes the {@code @Disabled} annotation and gets a red test to work against. The suite must
 * stay green in the meantime, which is why nothing here runs and why no test asserts the
 * vulnerable behaviour as if it were intended.
 *
 * <p>All the multi-pilot scaffolding lives in this one file rather than being spread across new
 * per-controller suites: {@link #owner} is the pilot who owns the records, {@link #attacker} is a
 * second logged-in pilot who owns nothing. Authentication is therefore never the thing under
 * test — the attacker always holds a valid Session. What is under test is Ownership.
 */
@SpringBootTest(classes = FloatyApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityAuditTest {

    private static final long OWNER_ID = 1L;
    private static final long ATTACKER_ID = 2L;
    private static final long OWNED_FLIGHT_ID = 100L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IFlightApplicationService flightApplicationService;

    @MockBean
    private IFlightRepository flightRepository;

    /** The pilot who owns the Flight under attack. */
    private User owner;

    /** A second logged-in pilot who owns nothing. Every exploit below runs as this pilot. */
    private User attacker;

    /** A Flight belonging to {@link #owner}, complete with IGC data. */
    private Flight ownedFlight;

    @BeforeEach
    public void setUp() {
        owner = pilot(OWNER_ID, "owner");
        attacker = pilot(ATTACKER_ID, "attacker");

        ownedFlight = new Flight();
        ownedFlight.setId(OWNED_FLIGHT_ID);
        ownedFlight.setUser(owner);
        ownedFlight.setFlightParameters(new FlightParameters(
                Instant.parse("2024-10-21T10:45:00Z"), 80L, "Owner's flight", null, null, null));

        IgcMetadata metadata = new IgcMetadata();
        metadata.setFileName("owner-home-coordinates.igc");
        IgcData igcData = new IgcData();
        igcData.setIgcMetadata(metadata);
        igcData.setFile("HFDTE211024\nB1045004715123N00824567EA0089000894\n".getBytes());
        ownedFlight.setIgcData(igcData);

        // The owner's flight is resolvable by id: every exploit below turns on the fact that
        // resolving it is not gated by who is asking.
        when(flightApplicationService.findFlightById(OWNED_FLIGHT_ID)).thenReturn(ownedFlight);

        // The attacker is authenticated throughout. Only Ownership is in question.
        authenticateAs(attacker);
    }

    private static User pilot(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(name + "@example.com");
        user.setEmailVerified(true);
        return user;
    }

    /** Puts a valid Session for {@code user} in the SecurityContext, as SessionTokenFilter does. */
    private static void authenticateAs(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    /**
     * FLOATY-SEC-001 — {@code DELETE /flights/{flightId}} enforces no Ownership check, so any
     * logged-in pilot can permanently delete another pilot's flight.
     *
     * <p>Expected once fixed: the attacker gets 403 (or 404), and the flight is never deleted.
     */
    @Test
    @Disabled("FLOATY-SEC-001: DELETE /flights/{id} does not check Ownership. Enable when fixed.")
    public void deleteFlight_byNonOwner_isRejected() throws Exception {
        mockMvc.perform(delete("/flights/{flightId}", OWNED_FLIGHT_ID).with(csrf()))
                .andExpect(status().isForbidden());

        verify(flightApplicationService, never()).deleteFlight(OWNED_FLIGHT_ID);
    }

    /**
     * FLOATY-SEC-002 — {@code PUT /flights/{flightId}} requires authentication but no Ownership,
     * so any logged-in pilot can overwrite the contents of another pilot's flight.
     *
     * <p>Expected once fixed: the attacker gets 403, and no update reaches the service.
     */
    @Test
    @Disabled("FLOATY-SEC-002: PUT /flights/{id} does not check Ownership. Enable when fixed.")
    public void updateFlight_byNonOwner_isRejected() throws Exception {
        when(flightRepository.findById(OWNED_FLIGHT_ID)).thenReturn(Optional.of(ownedFlight));

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "dateTime", "2024-10-21T10:45:00Z",
                "duration", 999,
                "description", "Overwritten by the attacker",
                "launchSpotId", 1,
                "landingSpotId", 2,
                "gliderId", 3));

        mockMvc.perform(put("/flights/{flightId}", OWNED_FLIGHT_ID).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());

        verify(flightApplicationService, never()).updateFlight(eq(OWNED_FLIGHT_ID), any());
    }

    /**
     * FLOATY-SEC-003 — {@code GET /flights/{flightId}} requires authentication but no Ownership,
     * so any logged-in pilot can read another pilot's flight record.
     *
     * <p>Expected once fixed: the attacker gets 403 (or 404), never the owner's flight.
     */
    @Test
    @Disabled("FLOATY-SEC-003: GET /flights/{id} does not check Ownership. Enable when fixed.")
    public void readFlight_byNonOwner_isRejected() throws Exception {
        mockMvc.perform(get("/flights/{flightId}", OWNED_FLIGHT_ID))
                .andExpect(status().isForbidden())
                // The owner's data must not appear in the body, whatever the status.
                .andExpect(content().string(not(containsString("Owner's flight"))));
    }

    /**
     * FLOATY-SEC-004 — {@code GET /flights/{flightId}/igc} returns the raw uploaded IGC file
     * with no Ownership check, disclosing another pilot's precise launch and landing coordinates.
     *
     * <p>Expected once fixed: the attacker gets 403 (or 404), never the owner's track file.
     */
    @Test
    @Disabled("FLOATY-SEC-004: GET /flights/{id}/igc does not check Ownership. Enable when fixed.")
    public void readIgcData_byNonOwner_isRejected() throws Exception {
        mockMvc.perform(get("/flights/{flightId}/igc", OWNED_FLIGHT_ID))
                .andExpect(status().isForbidden());
    }

    /**
     * FLOATY-SEC-004 — {@code GET /flights/{flightId}/track} is the parsed form of the same data
     * and is missing the same Ownership check.
     *
     * <p>Expected once fixed: the attacker gets 403 (or 404), never the owner's GPS track.
     */
    @Test
    @Disabled("FLOATY-SEC-004: GET /flights/{id}/track does not check Ownership. Enable when fixed.")
    public void readFlightTrack_byNonOwner_isRejected() throws Exception {
        mockMvc.perform(get("/flights/{flightId}/track", OWNED_FLIGHT_ID))
                .andExpect(status().isForbidden());
    }

    /**
     * FLOATY-SEC-024 — a request for a flight id that does not exist returns 500 rather than 404,
     * because {@code findFlightById} throws {@link IllegalArgumentException} (which has no handler)
     * instead of returning null. The 200-vs-500 split lets any logged-in pilot enumerate exactly
     * which flight ids exist.
     *
     * <p>Expected once fixed: a missing id is a 404, and no stack trace is logged for it.
     */
    @Test
    @Disabled("FLOATY-SEC-024: missing flight ids return 500, not 404. Enable when fixed.")
    public void readFlight_withUnknownId_isNotFound() throws Exception {
        long unknownId = 999_999L;
        when(flightApplicationService.findFlightById(unknownId))
                .thenThrow(new IllegalArgumentException("Flight not found"));

        mockMvc.perform(get("/flights/{flightId}", unknownId))
                .andExpect(status().isNotFound());
    }

    /**
     * FLOATY-SEC-016 — {@code POST /auth/initiate-password-reset} binds {@code @RequestBody String}
     * with no {@code consumes} restriction, so it accepts {@code text/plain}. That is a CORS-simple
     * content type, meaning a plain cross-site HTML form can trigger Floaty into sending reset mail
     * to any address with no preflight and no Floaty account.
     *
     * <p>Expected once fixed: only {@code application/json} is accepted, so a {@code text/plain}
     * body is rejected with 415 Unsupported Media Type.
     */
    @Test
    @Disabled("FLOATY-SEC-016: reset initiation accepts text/plain cross-site. Enable when fixed.")
    public void initiatePasswordReset_withTextPlain_isRejected() throws Exception {
        mockMvc.perform(post("/auth/initiate-password-reset").with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("victim@example.com"))
                .andExpect(status().isUnsupportedMediaType());
    }

    /**
     * FLOATY-SEC-020 — registration reports whether an email address already belongs to a pilot,
     * so anyone can test addresses for Floaty membership without an account.
     *
     * <p>Expected once fixed: the response does not distinguish a taken address from a free one.
     * Asserted here as "not 400 carrying the disclosing message"; the exact contract is for the fix
     * ticket to choose.
     */
    @Test
    @Disabled("FLOATY-SEC-020: registration discloses account existence. Enable when fixed.")
    public void register_withTakenEmail_doesNotDiscloseExistence() throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "username", "newpilot",
                "email", "already-registered@example.com",
                "password", "a-sufficiently-long-password"));

        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().string(not(containsString("already exists"))));
    }
}
