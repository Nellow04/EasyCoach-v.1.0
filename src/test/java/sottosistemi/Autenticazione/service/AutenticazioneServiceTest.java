package sottosistemi.Autenticazione.service;

import model.beans.Utente;
import model.dao.UtenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for AutenticazioneService")
class AutenticazioneServiceTest {

    @Mock
    private UtenteDAO utenteDAO;

    @InjectMocks
    private AutenticazioneService autenticazioneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        autenticazioneService = new AutenticazioneService(utenteDAO); // Iniezione del mock
    }

    @Test
    @DisplayName("TC_1.1: Validazione - Email non valida")
    void testValidaInputRegistrazione_EmailNonValida() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "invalid_email", "Mario", "Rossi", "hashedPassword123", "MENTOR");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC_1.2: Validazione - Nome troppo corto")
    void testValidaInputRegistrazione_NomeTroppoCorto() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "test@email.com", "M", "Rossi", "hashedPassword123", "MENTOR");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC_1.3: Validazione - Cognome troppo corto")
    void testValidaInputRegistrazione_CognomeTroppoCorto() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "test@email.com", "Mario", "R", "hashedPassword123", "MENTOR");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC_1.4: Validazione - Ruolo non valido")
    void testValidaInputRegistrazione_RuoloNonValido() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "test@email.com", "Mario", "Rossi", "hashedPassword123", "STUDENT");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC_1.5: Validazione - Password nulla")
    void testValidaInputRegistrazione_PasswordNulla() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "test@email.com", "Mario", "Rossi", null, "MENTOR");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC_1.6: Validazione - Email formato non valido")
    void testValidaInputRegistrazione_EmailFormatoInvalido() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "email@", "Mario", "Rossi", "hashedPassword123", "MENTOR");
        assertFalse(result);

        result = autenticazioneService.validaInputRegistrazione(
                "@domain.com", "Mario", "Rossi", "hashedPassword123", "MENTOR");
        assertFalse(result);

        result = autenticazioneService.validaInputRegistrazione(
                "email!@domain.com", "Mario", "Rossi", "hashedPassword123", "MENTOR");
        assertFalse(result);

        result = autenticazioneService.validaInputRegistrazione(
                "valid.email@domain.com", "Mario", "Rossi", "hashedPassword123", "MENTOR");
        assertTrue(result);
    }

    @Test
    @DisplayName("TC_1.7: Validazione - Tutti i campi validi")
    void testValidaInputRegistrazione_TuttiCampiValidi() {
        boolean result = autenticazioneService.validaInputRegistrazione(
                "test@email.com", "Mario", "Rossi", "hashedPassword123", "MENTOR");
        assertTrue(result);
    }

    @Test
    @DisplayName("TC_1.7.1: Registra Nuovo Utente")
    void testRegistraNuovoUtente() throws SQLException {
        doNothing().when(utenteDAO).doSave(any(Utente.class));

        autenticazioneService.registraNuovoUtente(
                "test@email.com", "Mario", "Rossi", "hashedPassword123", "MENTOR");

        verify(utenteDAO, times(1)).doSave(argThat(utente ->
                utente.getEmail().equals("test@email.com") &&
                        utente.getNome().equals("Mario") &&
                        utente.getCognome().equals("Rossi") &&
                        utente.getPassword().equals("hashedPassword123") &&
                        utente.getRuolo().equals("MENTOR")
        ));
    }

    @Test
    @DisplayName("TC_2.1: Verifica email - Email gia' registrata")
    void testIsEmailRegistrata_EmailGiaRegistrata() throws SQLException {
        Utente mockUtente = new Utente();
        mockUtente.setEmail("test@email.com");

        when(utenteDAO.doRetrieveByEmail("test@email.com")).thenReturn(mockUtente);

        boolean result = autenticazioneService.isEmailRegistrata("test@email.com");
        assertTrue(result);
    }

    @Test
    @DisplayName("TC_2.2: Verifica email - Email non registrata")
    void testIsEmailRegistrata_EmailNonRegistrata() throws SQLException {
        when(utenteDAO.doRetrieveByEmail("test@email.com")).thenReturn(null);

        boolean result = autenticazioneService.isEmailRegistrata("test@email.com");
        assertFalse(result);
    }



    @Test
    @DisplayName("TC_3.1: Hash Password - Valida")
    void testHashPassword_Valida() throws Exception {
        String password = "mypassword123";
        String hashedPassword = autenticazioneService.hashPassword(password);

        assertNotNull(hashedPassword);
        assertEquals(64, hashedPassword.length()); // Lunghezza SHA-256 in formato esadecimale
    }

    @Test
    @DisplayName("TC_3.2: Hash Password - Password nulla")
    void testHashPassword_PasswordNulla() {
        assertThrows(Exception.class, () -> autenticazioneService.hashPassword(null));
    }

    @Test
    @DisplayName("TC_4.1: Effettua Login - Password errata")
    void testEffettuaLogin_PasswordErrata() throws Exception {
        Utente utente = new Utente();
        utente.setPassword(autenticazioneService.hashPassword("correctpassword"));
        when(utenteDAO.doRetrieveByEmail("test@email.com")).thenReturn(utente);

        Utente result = autenticazioneService.effettuaLogin("test@email.com", "wrongpassword");
        assertNull(result);
    }

    @Test
    @DisplayName("TC_4.2: Effettua Login - Email non trovata")
    void testEffettuaLogin_EmailNonTrovata() throws Exception {
        when(utenteDAO.doRetrieveByEmail("test@email.com")).thenReturn(null);

        Utente result = autenticazioneService.effettuaLogin("test@email.com", "mypassword123");
        assertNull(result);
    }


    @Test
    @DisplayName("TC_4.3: Effettua Login - Credenziali valide")
    void testEffettuaLogin_CredenzialiValide() throws Exception {
        Utente utente = new Utente();
        utente.setPassword(autenticazioneService.hashPassword("mypassword123"));
        when(utenteDAO.doRetrieveByEmail("test@email.com")).thenReturn(utente);

        Utente result = autenticazioneService.effettuaLogin("test@email.com", "mypassword123");
        assertNotNull(result);
    }

}
