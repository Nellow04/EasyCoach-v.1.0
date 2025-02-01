package sottosistemi.Prenotazione.controller;

import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.beans.Pagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Prenotazione.service.PaymentService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for PaymentServlet")
class PaymentServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentServlet paymentServlet;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test per il metodo doPost

    @Test
    @DisplayName("TC_1.1: Utente con ruolo MENTOR")
    void testDoPost_RuoloMentor() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("MENTOR");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("I mentor non possono effettuare prenotazioni"));
    }

    @Test
    @DisplayName("TC_1.2: Metodo di pagamento non specificato")
    void testDoPost_MetodoPagamentoNonSpecificato() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("USER");
        when(request.getParameter("metodoPagamento")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("Metodo di pagamento non specificato"));
    }

    @Test
    @DisplayName("TC_1.3: Metodo di pagamento vuoto o solo spazi")
    void testDoPost_MetodoPagamentoVuotoOSpazi() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("USER");
        when(request.getParameter("metodoPagamento")).thenReturn("   ");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("Metodo di pagamento non specificato"));
    }

    @Test
    @DisplayName("TC_1.4: Pagamento con CARTA - Parametri corretti")
    void testDoPost_PagamentoConCarta_Valido() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("USER");
        when(request.getParameter("metodoPagamento")).thenReturn("CARTA");
        when(request.getParameter("numeroCarta")).thenReturn("1234567812345678");
        when(request.getParameter("scadenzaGGMM")).thenReturn("12");
        when(request.getParameter("scadenzaAnno")).thenReturn("2025");
        when(request.getParameter("cardHolder")).thenReturn("Mario Rossi");
        when(request.getParameter("cvv")).thenReturn("123");
        when(request.getParameter("idPrenotazione")).thenReturn("1");
        when(request.getParameter("totalePagato")).thenReturn("50.0");
        when(session.getAttribute("idUtente")).thenReturn("2");

        Pagamento mockPagamento = new Pagamento();
        when(paymentService.processPayment(1, "CARTA", 50.0, "2")).thenReturn(mockPagamento);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("Pagamento completato con successo"));
    }

    @Test
    @DisplayName("TC_1.5: Pagamento con metodo diverso da CARTA")
    void testDoPost_MetodoPagamentoDiversoDaCarta() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("USER");
        when(request.getParameter("metodoPagamento")).thenReturn("PAYPAL");
        when(request.getParameter("idPrenotazione")).thenReturn("1");
        when(request.getParameter("totalePagato")).thenReturn("50.0");
        when(session.getAttribute("idUtente")).thenReturn("2");

        Pagamento mockPagamento = new Pagamento();
        when(paymentService.processPayment(1, "PAYPAL", 50.0, "2")).thenReturn(mockPagamento);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("Pagamento completato con successo"));
    }

    @Test
    @DisplayName("TC_1.6: Pagamento non completato (pagamento null)")
    void testDoPost_PagamentoNull() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("USER");
        when(request.getParameter("metodoPagamento")).thenReturn("CARTA");
        when(request.getParameter("idPrenotazione")).thenReturn("1");
        when(request.getParameter("totalePagato")).thenReturn("50.0");
        when(session.getAttribute("idUtente")).thenReturn("2");

        when(paymentService.processPayment(1, "CARTA", 50.0, "2")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        String jsonResponse = stringWriter.toString();
        assertFalse(jsonResponse.contains("Errore durante il salvataggio del pagamento"));
    }

    @Test
    @DisplayName("TC_1.7: Eccezione imprevista durante il pagamento")
    void testDoPost_EccezioneImprevista() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("USER");
        when(request.getParameter("metodoPagamento")).thenReturn("CARTA");
        when(request.getParameter("idPrenotazione")).thenReturn("1");
        when(request.getParameter("totalePagato")).thenReturn("50.0");
        when(session.getAttribute("idUtente")).thenReturn("2");

        doThrow(new RuntimeException("Errore imprevisto")).when(paymentService)
                .processPayment(1, "CARTA", 50.0, "2");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        paymentServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("Si è verificato un errore imprevisto"));
    }
}


