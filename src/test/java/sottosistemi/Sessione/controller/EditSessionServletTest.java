package sottosistemi.Sessione.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.beans.Sessione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Sessione.service.ImageService;
import sottosistemi.Sessione.service.SessionManagementService;
import sottosistemi.Sessione.service.SessionValidationService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for EditSessionServlet")
class EditSessionServletTest {
    private static final String PERMANENT_UPLOAD_PATH = System.getProperty("user.home") + File.separator + "easycoach_uploads";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private SessionManagementService sessionManagementService;

    @Mock
    private SessionValidationService validationService;

    @Mock
    private ImageService imageService;

    @Mock
    private Part imagePart;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private EditSessionServlet editSessionServlet;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    @DisplayName("TC_1.1: Utente non autorizzato")
    void testUnauthorizedUser() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(null);
        when(session.getAttribute("ruolo")).thenReturn("STUDENT");

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Utente non autorizzato");
    }

    @Test
    @DisplayName("TC_1.2: Utente autorizzato")
    void testAuthorizedUser() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("load");

        editSessionServlet.doPost(request, response);

        // No error is thrown
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("TC_2.1: Azione non specificata")
    void testActionNotSpecified() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn(null);

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer).write("Azione non specificata");
    }

    @Test
    @DisplayName("TC_2.2: Azione non valida")
    void testInvalidAction() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("invalidAction");

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer).write("Azione non valida");
    }

    @Test
    @DisplayName("TC_3.1: Caricamento di una sessione")
    void testLoadSession() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("load");
        when(request.getParameter("idSessione")).thenReturn("123");

        Sessione sessione = new Sessione();
        sessione.setIdUtente(1);
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);
        when(sessionManagementService.findTimeslotsBySessionId(123)).thenReturn(List.of());

        editSessionServlet.doPost(request, response);

        // Verifica che il JSON della risposta contenga la sessione
        verify(writer).write(contains("sessione"));
    }

    @Test
    @DisplayName("TC_3.2: Salvataggio di una sessione valido")
    void testSaveSessionValid() throws Exception {
        // Configurazione dei mock
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("idSessione")).thenReturn("123");
        when(request.getParameter("nome")).thenReturn("Titolo valido");
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("100.00"); // Simula un valore per prezzo
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});

        // Configura sessione esistente
        Sessione sessione = new Sessione();
        sessione.setIdUtente(1);
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);

        // Nessun errore nella validazione
        when(validationService.validateFormEdit(anyString(), anyString(), anyString(), any(), any())).thenReturn(new HashMap<>());

        // Esecuzione del metodo
        editSessionServlet.doPost(request, response);

        // Verifica che la sessione venga aggiornata
        verify(sessionManagementService).updateSession(any(), any(), any());
        verify(writer).write(contains("success"));
    }


    @Test
    @DisplayName("TC_3.3: Salvataggio con immagine non valida")
    void testSaveSessionInvalidImage() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("idSessione")).thenReturn("123");
        when(request.getPart("immagine")).thenReturn(imagePart);
        when(imagePart.getSize()).thenReturn(1024L);

        when(imageService.validateImage(imagePart)).thenReturn(false);

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer).write(contains("Il formato del file non è valido"));
    }

    @Test
    @DisplayName("TC_3.4: Salvataggio con parametri non validi")
    void testSaveSessionInvalidParameters() throws Exception {
        // Configurazione dell'utente autorizzato
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("idSessione")).thenReturn("123");
        when(request.getParameter("nome")).thenReturn(""); // Simula parametro non valido
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("100.00"); // Simula un valore per prezzo
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});

        // Configurazione della sessione esistente
        Sessione sessione = new Sessione();
        sessione.setIdUtente(1); // L'utente è autorizzato a modificare la sessione
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);

        // Simulazione di errori di validazione
        Map<String, String> errors = new HashMap<>();
        errors.put("nome", "Il nome è obbligatorio"); // Errore fittizio
        when(validationService.validateFormEdit(anyString(), anyString(), anyString(), any(), any())).thenReturn(errors);

        // Esecuzione del metodo
        editSessionServlet.doPost(request, response);

        // Verifica che venga restituito lo stato HTTP 400
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // Verifica che la risposta contenga gli errori
        verify(writer).write(contains("errors"));
    }



    @Test
    @DisplayName("TC_3.5: Eliminazione di una sessione senza conflitti")
    void testDeleteSessionNoConflicts() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("idSessione")).thenReturn("123");

        Sessione sessione = new Sessione();
        sessione.setIdUtente(1);
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);
        when(sessionManagementService.hasActiveBookings(123)).thenReturn(false);

        editSessionServlet.doPost(request, response);

        verify(sessionManagementService).archiveSession(sessione);
        verify(writer).write(contains("Sessione archiviata con successo"));
    }

    @Test
    @DisplayName("TC_3.6: Eliminazione con conflitti")
    void testDeleteSessionWithConflicts() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("idSessione")).thenReturn("123");

        Sessione sessione = new Sessione();
        sessione.setIdUtente(1);
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);
        when(sessionManagementService.hasActiveBookings(123)).thenReturn(true);

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(writer).write(contains("Ci sono prenotazioni attive"));
    }

    @Test
    @DisplayName("TC_4.1: Gestione errore SQL in loadSession")
    void testSQLExceptionInLoadSession() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("load");
        when(request.getParameter("idSessione")).thenReturn("123");

        // Simula l'eccezione SQL
        when(sessionManagementService.findSessionById(123)).thenThrow(new SQLException("Database error"));

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).write(contains("Errore del server"));
    }

    @Test
    @DisplayName("TC_4.2: Gestione errore generico")
    void testGeneralExceptionInLoadSession() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("load");
        when(request.getParameter("idSessione")).thenReturn("123");

        // Simula un'eccezione generica
        when(sessionManagementService.findSessionById(123)).thenThrow(new RuntimeException("Generic error"));

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).write(contains("Errore imprevisto"));
    }

    @Test
    @DisplayName("TC_4.3: Upload immagine con successo")
    void testSuccessfulImageUpload() throws Exception {
        // Setup autenticazione
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("idSessione")).thenReturn("123");

        // Setup sessione esistente
        Sessione sessione = new Sessione();
        sessione.setIdUtente(1);
        sessione.setImmagine("old_image.jpg");
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);

        // Setup immagine
        when(request.getPart("immagine")).thenReturn(imagePart);
        when(imagePart.getSize()).thenReturn(1024L);
        when(imageService.validateImage(imagePart)).thenReturn(true);
        when(imageService.processImageUpload(any(), any(), any())).thenReturn("new_image.jpg");

        // Setup altri parametri necessari
        when(request.getParameter("nome")).thenReturn("Test Session");
        when(request.getParameter("descrizione")).thenReturn("Test Description");
        when(request.getParameter("prezzo")).thenReturn("100.00");
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});
        when(validationService.validateFormEdit(any(), any(), any(), any(), any())).thenReturn(new HashMap<>());

        editSessionServlet.doPost(request, response);

        // Verifica che l'immagine vecchia sia stata eliminata
        verify(imageService).deleteImage("old_image.jpg", PERMANENT_UPLOAD_PATH);
        verify(writer).write(contains("success"));
    }

    @Test
    @DisplayName("TC_4.4: Test accesso non autorizzato alla sessione in load")
    void testUnauthorizedSessionAccess() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("load");
        when(request.getParameter("idSessione")).thenReturn("123");

        // Crea una sessione appartenente a un altro utente
        Sessione sessione = new Sessione();
        sessione.setIdUtente(2); // ID diverso dal mentor corrente
        when(sessionManagementService.findSessionById(123)).thenReturn(sessione);

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(writer).write("Non autorizzato ad accedere a questa sessione");
    }

    @Test
    @DisplayName("TC_4.5: Test sessione null in load")
    void testNullSessionAccess() throws Exception {
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(request.getParameter("action")).thenReturn("load");
        when(request.getParameter("idSessione")).thenReturn("123");

        // Simula una sessione non trovata
        when(sessionManagementService.findSessionById(123)).thenReturn(null);

        editSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(writer).write("Non autorizzato ad accedere a questa sessione");
    }

}
