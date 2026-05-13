package com.demoApp.demoApp.service;

import com.demoApp.demoApp.config.GoogleCalendarProperties;
import com.demoApp.demoApp.entity.Appointment;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS);
    private static final String TOKENS_KEY = "admin";

    private final GoogleCalendarProperties props;

    public GoogleCalendarService(GoogleCalendarProperties props) {
        this.props = props;
    }

    public String buildAuthorizationUrl() {
        GoogleAuthorizationCodeFlow flow = buildFlow();
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl()
                .setRedirectUri(props.getRedirectUri())
                .setAccessType("offline")
                .setApprovalPrompt("force");
        // Lightweight CSRF-ish token so callback can't be trivially replayed without a session;
        // we keep it simple: caller can pass it back as "state" if they want to validate later.
        url.setState(UUID.randomUUID().toString());
        return url.build();
    }

    public void handleOAuthCallback(String code) {
        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(props.getRedirectUri())
                    .execute();
            flow.createAndStoreCredential(tokenResponse, TOKENS_KEY);
        } catch (Exception ex) {
            throw new RuntimeException("Google OAuth callback failed", ex);
        }
    }

    public boolean isConnected() {
        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            return flow.loadCredential(TOKENS_KEY) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    @Async
    public void createEventForAppointment(Appointment appointment, String createdBy) {
        try {
            if (appointment == null) {
                return;
            }
            GoogleAuthorizationCodeFlow flow = buildFlow();
            var credential = flow.loadCredential(TOKENS_KEY);
            if (credential == null) {
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .warn("Google Calendar not connected; skipping event insert");
                return;
            }

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Calendar calendar = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("demoApp")
                    .build();

            String clientName = appointment.getClient() != null
                    ? (appointment.getClient().getFirstName() + " " + appointment.getClient().getLastName())
                    : "(sin cliente)";
            String techName = appointment.getTechnician() != null
                    ? (appointment.getTechnician().getName() + " " + appointment.getTechnician().getLastName())
                    : "(sin tecnico)";

            String services = "";
            if (appointment.getServices() != null && !appointment.getServices().isEmpty()) {
                services = appointment.getServices().stream()
                        .map(aps -> aps.getService() != null ? aps.getService().getName() : null)
                        .filter(n -> n != null && !n.isBlank())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            }

            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime start = appointment.getStartAt() != null ? appointment.getStartAt().atZone(zone) : null;
            ZonedDateTime end = appointment.getEndAt() != null ? appointment.getEndAt().atZone(zone) : null;

            Event event = new Event();
            event.setSummary("Cita: " + clientName);
            String desc = "Cliente: " + clientName + "\n"
                    + "Tecnico: " + techName + "\n"
                    + "Servicios: " + (services.isBlank() ? "-" : services) + "\n"
                    + "Status: " + (appointment.getStatus() != null ? appointment.getStatus().name() : "-") + "\n"
                    + "Creada por: " + (createdBy != null ? createdBy : "-") + "\n";
            event.setDescription(desc);

            if (start != null) {
                EventDateTime s = new EventDateTime();
                s.setDateTime(new com.google.api.client.util.DateTime(start.toInstant().toEpochMilli()));
                s.setTimeZone(zone.getId());
                event.setStart(s);
            }
            if (end != null) {
                EventDateTime e = new EventDateTime();
                e.setDateTime(new com.google.api.client.util.DateTime(end.toInstant().toEpochMilli()));
                e.setTimeZone(zone.getId());
                event.setEnd(e);
            }

            calendar.events().insert(props.getCalendarId(), event).execute();
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("Failed to insert Google Calendar event", ex);
        }
    }

    private GoogleAuthorizationCodeFlow buildFlow() {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            File tokensDir = new File(Optional.ofNullable(props.getTokensPath()).orElse("./tokens"));
            FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(tokensDir);

            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    props.getClientId(),
                    props.getClientSecret(),
                    SCOPES
            )
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType("offline")
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to build Google OAuth flow", ex);
        }
    }
}

