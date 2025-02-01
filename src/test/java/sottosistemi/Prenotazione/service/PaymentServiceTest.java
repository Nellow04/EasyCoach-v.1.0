package sottosistemi.Prenotazione.service;

import model.beans.Pagamento;
import model.beans.Prenotazione;
import model.beans.Sessione;
import model.dao.PagamentoDAO;
import model.dao.PrenotazioneDAO;
import model.dao.SessioneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sottosistemi.Prenotazione.service.PaymentService.ValidationException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for PaymentService")
class PaymentServiceTest {

    @Mock
    private PagamentoDAO pagamentoDAO;

    @Mock
    private PrenotazioneDAO prenotazioneDAO;

    @Mock
    private SessioneDAO sessioneDAO;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Validazione numero carta valido")
    void testValidateCardPayment_NumeroCartaValido() {
        assertDoesNotThrow(() -> paymentService.validateCardPayment("1234567812345678", "01/12", "2030", "Mario Rossi", "123"));
    }

    @Test
    @DisplayName("TC_1.2: Numero carta non valido")
    void testValidateCardPayment_NumeroCartaNonValido() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234", "01/12", "2030", "Mario Rossi", "123"));
        assertEquals("Numero carta non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.3: Data di scadenza carta non valida")
    void testValidateCardPayment_DataScadenzaNonValida() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "32/13", "2030", "Mario Rossi", "123"));
        assertEquals("Formato data scadenza non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.4: Carta scaduta")
    void testValidateCardPayment_CartaScaduta() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "01/12", "2020", "Mario Rossi", "123"));
        assertEquals("Data di scadenza non valida", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.5: Nome titolare non valido")
    void testValidateCardPayment_NomeTitolareNonValido() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "01/12", "2030", "@InvalidName", "123"));
        assertEquals("Nome titolare carta non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.6: CVV non valido")
    void testValidateCardPayment_CVVNonValido() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "01/12", "2030", "Mario Rossi", "12"));
        assertEquals("CVV non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_2.1: Processa pagamento valido")
    void testProcessPayment_Valido() throws SQLException {
        int idPrenotazione = 1;
        String metodoPagamento = "CARTA";
        double totalePagato = 100.0;
        String idUtenteSession = "10";

        Prenotazione mockPrenotazione = new Prenotazione();
        mockPrenotazione.setIdPrenotazione(idPrenotazione);
        mockPrenotazione.setIdSessione(2);

        when(prenotazioneDAO.doFindById(idPrenotazione)).thenReturn(mockPrenotazione);
        doNothing().when(pagamentoDAO).doSave(any(Pagamento.class));
        doNothing().when(prenotazioneDAO).doUpdate(mockPrenotazione);

        Pagamento result = paymentService.processPayment(idPrenotazione, metodoPagamento, totalePagato, idUtenteSession);

        assertNotNull(result);
        assertEquals("COMPLETATO", result.getStatusPagamento());
    }

    @Test
    @DisplayName("TC_2.2: Prenotazione non trovata")
    void testProcessPayment_PrenotazioneNonTrovata() throws SQLException {
        int idPrenotazione = 1;
        String metodoPagamento = "CARTA";
        double totalePagato = 100.0;
        String idUtenteSession = "10";

        when(prenotazioneDAO.doFindById(idPrenotazione)).thenReturn(null);

        Pagamento result = paymentService.processPayment(idPrenotazione, metodoPagamento, totalePagato, idUtenteSession);

        assertNotNull(result);
        assertEquals("COMPLETATO", result.getStatusPagamento());
    }

    @Test
    @DisplayName("TC_2.3: SQLException durante il salvataggio del pagamento")
    void testProcessPayment_SQLException() throws SQLException {
        int idPrenotazione = 1;
        String metodoPagamento = "CARTA";
        double totalePagato = 100.0;
        String idUtenteSession = "10";

        doThrow(new SQLException()).when(pagamentoDAO).doSave(any(Pagamento.class));

        assertThrows(SQLException.class, () -> paymentService.processPayment(idPrenotazione, metodoPagamento, totalePagato, idUtenteSession));
    }

    @Test
    @DisplayName("TC_1.1: Numero carta nullo")
    void testValidateCardPayment_NumeroCartaNullo() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment(null, "01/12", "2030", "Mario Rossi", "123"));
        assertEquals("Numero carta non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.2: Data scadenza con formato errato")
    void testValidateCardPayment_DataScadenzaFormatoErrato() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "13/2022", "2030", "Mario Rossi", "123"));
        assertEquals("Formato data scadenza non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.3: Nome titolare nullo")
    void testValidateCardPayment_NomeTitolareNullo() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "01/12", "2030", null, "123"));
        assertEquals("Nome titolare carta non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_1.4: CVV nullo")
    void testValidateCardPayment_CVVNullo() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                paymentService.validateCardPayment("1234567812345678", "01/12", "2030", "Mario Rossi", null));
        assertEquals("CVV non valido", exception.getMessage());
    }

    @Test
    @DisplayName("TC_2.1: Metodo pagamento GOOGLEPAY")
    void testProcessPayment_MetodoGooglePay() throws SQLException {
        int idPrenotazione = 1;
        String metodoPagamento = "GOOGLEPAY";
        double totalePagato = 100.0;
        String idUtenteSession = "10";

        Prenotazione mockPrenotazione = new Prenotazione();
        mockPrenotazione.setIdPrenotazione(idPrenotazione);
        mockPrenotazione.setIdSessione(2);

        when(prenotazioneDAO.doFindById(idPrenotazione)).thenReturn(mockPrenotazione);
        doNothing().when(pagamentoDAO).doSave(any(Pagamento.class));
        doNothing().when(prenotazioneDAO).doUpdate(mockPrenotazione);

        Pagamento result = paymentService.processPayment(idPrenotazione, metodoPagamento, totalePagato, idUtenteSession);

        assertNotNull(result);
        assertEquals("ALTRO", result.getMetodoPagamento());
    }

    @Test
    @DisplayName("TC_3.1: Sessione nulla in sendNotifications")
    void testSendNotifications_SessioneNulla() throws SQLException {
        Prenotazione mockPrenotazione = new Prenotazione();
        mockPrenotazione.setIdPrenotazione(1);
        mockPrenotazione.setIdSessione(1);

        when(prenotazioneDAO.doFindById(1)).thenReturn(mockPrenotazione);
        when(sessioneDAO.doFindById(mockPrenotazione.getIdSessione())).thenReturn(null);

        paymentService.processPayment(1, "CARTA", 100.0, "10");

        verify(sessioneDAO).doFindById(mockPrenotazione.getIdSessione());
    }


    @Test
    @DisplayName("TC_3.2: ID utente sessione nullo in sendNotifications")
    void testSendNotifications_IdUtenteSessioneNullo() throws SQLException {
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setIdSessione(1);

        Sessione mockSessione = new Sessione();
        mockSessione.setIdUtente(1);

        when(prenotazioneDAO.doFindById(1)).thenReturn(prenotazione);
        when(sessioneDAO.doFindById(prenotazione.getIdSessione())).thenReturn(mockSessione);

        paymentService.processPayment(1, "CARTA", 100.0, null);

        verify(sessioneDAO).doFindById(prenotazione.getIdSessione());
    }

}
