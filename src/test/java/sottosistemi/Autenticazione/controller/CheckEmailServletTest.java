package sottosistemi.Autenticazione.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Autenticazione.service.AutenticazioneService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@DisplayName("Test Cases for CheckEmailServlet")
class CheckEmailServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AutenticazioneService autenticazioneService;

    @InjectMocks
    private CheckEmailServlet checkEmailServlet;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("TC_4.1: Email valida - Email esistente")
    void testDoPost_EmailValida_Esistente() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(autenticazioneService.checkEmailExists(anyString())).thenReturn(true);

        // Test del comportamento del mock
        boolean mockResult = autenticazioneService.checkEmailExists("test@email.com");
        System.out.println("Mock restituisce: " + mockResult);

        checkEmailServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        System.out.println("Risultato JSON: " + jsonResponse);

        assertEquals("{\"exists\":true}", jsonResponse);
    }



    @Test
    @DisplayName("TC_4.2: Email valida - Email non esistente")
    void testDoPost_EmailValida_NonEsistente() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(autenticazioneService.checkEmailExists("test@email.com")).thenReturn(false);

        checkEmailServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertEquals("{\"exists\":false}", jsonResponse);
    }

    @Test
    @DisplayName("TC_4.3: Email vuota - Email non valida")
    void testDoPost_EmailVuota() throws Exception {
        when(request.getParameter("email")).thenReturn("");

        checkEmailServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertEquals("{\"exists\":false}", jsonResponse); // Ora riflette la validazione nella servlet
    }


    @Test
    @DisplayName("TC_4.4: Email nulla - Email non valida")
    void testDoPost_EmailNulla() throws Exception {
        when(request.getParameter("email")).thenReturn(null);

        checkEmailServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertEquals("{\"exists\":false}", jsonResponse); // Ora riflette la validazione nella servlet
    }


    @Test
    @DisplayName("TC_4.5: Eccezione durante il controllo - Errore generico")
    void testDoPost_EccezioneGenerica() throws Exception {
        when(request.getParameter("email")).thenReturn("test@email.com");
        when(autenticazioneService.checkEmailExists("test@email.com")).thenThrow(new RuntimeException());

        checkEmailServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertEquals("{\"exists\":false}", jsonResponse); // Le eccezioni vengono gestite come false
    }

}
