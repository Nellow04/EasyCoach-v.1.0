package sottosistemi.AreaUtente.service;

import model.beans.Utente;
import model.dao.PrenotazioneDAO;
import model.dao.UtenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for AccountService")
class AccountServiceTest {

    @Mock
    private UtenteDAO utenteDAO;

    @Mock
    private PrenotazioneDAO prenotazioneDAO;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: L'utente ha prenotazioni attive")
    void testHasActiveBookingsForUser_WithBookings() throws SQLException {
        when(prenotazioneDAO.hasActiveBookingsForUser(1)).thenReturn(true);
        assertTrue(accountService.hasActiveBookingsForUser(1));
    }

    @Test
    @DisplayName("TC_1.2: L'utente non ha prenotazioni attive")
    void testHasActiveBookingsForUser_NoBookings() throws SQLException {
        when(prenotazioneDAO.hasActiveBookingsForUser(1)).thenReturn(false);
        assertFalse(accountService.hasActiveBookingsForUser(1));
    }

    @Test
    @DisplayName("TC_1.3: Errore nel DAO")
    void testHasActiveBookingsForUser_DAOError() throws SQLException {
        when(prenotazioneDAO.hasActiveBookingsForUser(1)).thenThrow(new SQLException("Errore DB"));
        assertThrows(SQLException.class, () -> accountService.hasActiveBookingsForUser(1));
    }

    @Test
    @DisplayName("TC_2.1: Eliminazione utente riuscita")
    void testDeleteUser_Success() throws SQLException {
        doNothing().when(utenteDAO).doDelete(1);
        assertDoesNotThrow(() -> accountService.deleteUser(1));
    }

    @Test
    @DisplayName("TC_2.2: Errore nel DAO durante eliminazione")
    void testDeleteUser_DAOError() throws SQLException {
        doThrow(new SQLException("Errore DB")).when(utenteDAO).doDelete(1);
        assertThrows(SQLException.class, () -> accountService.deleteUser(1));
    }

    @Test
    @DisplayName("TC_3.1: Aggiornamento password riuscito")
    void testUpdateUserPassword_Success() throws Exception {
        Utente utente = new Utente();
        doNothing().when(utenteDAO).doUpdatePassword(utente);
        assertDoesNotThrow(() -> accountService.updateUserPassword(utente));
    }

    @Test
    @DisplayName("TC_3.2: Errore nel DAO durante aggiornamento password")
    void testUpdateUserPassword_DAOError() throws Exception {
        Utente utente = new Utente();
        doThrow(new SQLException("Errore DB")).when(utenteDAO).doUpdatePassword(utente);
        assertThrows(SQLException.class, () -> accountService.updateUserPassword(utente));
    }

    @Test
    @DisplayName("TC_4.1: Hashing password riuscito")
    void testHashPassword_Success() throws Exception {
        String password = "password123";
        assertNotNull(accountService.hashPassword(password));
    }

    @Test
    @DisplayName("TC_4.2: Errore durante hashing password")
    void testHashPassword_Error() {
        assertThrows(Exception.class, () -> accountService.hashPassword(null));
    }
}
