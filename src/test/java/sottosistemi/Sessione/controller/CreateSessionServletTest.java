package sottosistemi.Sessione.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Sessione.service.ImageService;
import sottosistemi.Sessione.service.SessionManagementService;
import sottosistemi.Sessione.service.SessionValidationService;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@DisplayName("Test Cases for CreateSessionServlet")
class CreateSessionServletTest {

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
    private CreateSessionServlet createSessionServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("session.jsp")).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("TC_1: Accesso negato per utenti non mentor")
    void testAccessDeniedForNonMentor() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("STUDENT");

        createSessionServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Solo i mentor possono creare sessioni");
    }

    @Test
    @DisplayName("TC_2: Accesso negato per utenti non autenticati")
    void testAccessDeniedForUnauthenticatedUser() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(null);

        createSessionServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autenticato");
    }

    @Test
    @DisplayName("TC_3: Parametri non validi")
    void testInvalidParameters() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("titolo")).thenReturn("");
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("100.00");
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});
        when(request.getPart("immagine")).thenReturn(imagePart);
        when(imagePart.getSize()).thenReturn(1024L);

        Map<String, String> errors = new HashMap<>();
        errors.put("titolo", "Il titolo è obbligatorio");
        when(validationService.validateForm(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(errors);

        when(response.getWriter()).thenReturn(writer);

        createSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer).print(contains("Il titolo è obbligatorio"));
    }

    @Test
    @DisplayName("TC_4: Immagine non valida")
    void testInvalidImage() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("titolo")).thenReturn("Titolo valido");
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("100.00");
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});
        when(request.getPart("immagine")).thenReturn(imagePart);
        when(imagePart.getSize()).thenReturn(1024L);

        when(validationService.validateForm(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(new HashMap<>());
        when(imageService.validateImage(imagePart)).thenReturn(false);

        when(response.getWriter()).thenReturn(writer);

        createSessionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer).print(contains("L'immagine deve essere JPG/JPEG/PNG/GIF, max 10MB"));
    }

    @Test
    @DisplayName("TC_5: Creazione sessione riuscita")
    void testSuccessfulSessionCreation() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("titolo")).thenReturn("Titolo valido");
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("100.00");
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});
        when(request.getPart("immagine")).thenReturn(imagePart);
        when(imagePart.getSize()).thenReturn(1024L);

        when(validationService.validateForm(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(new HashMap<>());
        when(imageService.validateImage(imagePart)).thenReturn(true);
        when(imageService.processImageUpload(eq(imagePart), anyString(), anyString())).thenReturn("image.jpg");

        createSessionServlet.doPost(request, response);

        verify(sessionManagementService).createSession(any(), any(), any());
        verify(response).sendRedirect("dashboardMentor.jsp?success=true");
    }

    @Test
    @DisplayName("TC_6: Eccezione SQL durante la creazione della sessione")
    void testSQLExceptionDuringSessionCreation() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("titolo")).thenReturn("Titolo valido");
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("100.00");
        when(request.getParameterValues("timeslot_day[]")).thenReturn(new String[]{"Lunedì"});
        when(request.getParameterValues("timeslot_hour[]")).thenReturn(new String[]{"10:00"});
        when(request.getPart("immagine")).thenReturn(imagePart);
        when(imagePart.getSize()).thenReturn(1024L);

        when(validationService.validateForm(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(new HashMap<>());
        when(imageService.validateImage(imagePart)).thenReturn(true);
        doThrow(new SQLException("Errore SQL")).when(sessionManagementService).createSession(any(), any(), any());

        createSessionServlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), contains("Errore durante la creazione della sessione"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("TC_7: Formato prezzo non valido")
    void testInvalidPriceFormat() throws Exception {
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");
        when(session.getAttribute("idUtente")).thenReturn(1);
        when(request.getParameter("titolo")).thenReturn("Titolo valido");
        when(request.getParameter("descrizione")).thenReturn("Descrizione valida");
        when(request.getParameter("prezzo")).thenReturn("invalid");

        createSessionServlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), contains("Formato del prezzo non valido"));
        verify(dispatcher).forward(request, response);
    }
}
