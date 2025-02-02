package sottosistemi.AreaUtente.service;

import model.beans.Sessione;
import model.beans.Utente;
import model.dao.SessioneDAO;
import model.dao.UtenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for HomeService")
class HomeServiceTest {

    @Mock
    private SessioneDAO sessioneDAO;

    @Mock
    private UtenteDAO utenteDAO;

    @InjectMocks
    private HomeService homeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Recupero sessioni in evidenza riuscito")
    void testGetSessioniInEvidenza_Success() throws SQLException {
        when(sessioneDAO.getTreSessioniCasuali()).thenReturn(List.of(new Sessione()));
        assertFalse(homeService.getSessioniInEvidenza().isEmpty());
    }

    @Test
    @DisplayName("TC_1.2: Nessuna sessione in evidenza")
    void testGetSessioniInEvidenza_Empty() throws SQLException {
        when(sessioneDAO.getTreSessioniCasuali()).thenReturn(List.of());
        assertTrue(homeService.getSessioniInEvidenza().isEmpty());
    }

    @Test
    @DisplayName("TC_1.3: Errore nel DAO")
    void testGetSessioniInEvidenza_DAOError() {
        when(sessioneDAO.getTreSessioniCasuali()).thenThrow(new RuntimeException("Errore DB"));
        assertThrows(RuntimeException.class, () -> homeService.getSessioniInEvidenza());
    }


    @Test
    @DisplayName("TC_2.1: Recupero mentor casuali riuscito")
    void testGetMentorCasuali_Success() throws SQLException {
        when(utenteDAO.getAllMentors()).thenReturn(List.of(new Utente(), new Utente(), new Utente()));
        assertEquals(3, homeService.getMentorCasuali().size());
    }

    @Test
    @DisplayName("TC_2.2: Meno di tre mentor disponibili")
    void testGetMentorCasuali_LessThanThree() throws SQLException {
        when(utenteDAO.getAllMentors()).thenReturn(List.of(new Utente()));
        assertEquals(1, homeService.getMentorCasuali().size());
    }

    @Test
    @DisplayName("TC_2.3: Nessun mentor disponibile")
    void testGetMentorCasuali_Empty() throws SQLException {
        when(utenteDAO.getAllMentors()).thenReturn(List.of());
        assertTrue(homeService.getMentorCasuali().isEmpty());
    }

    @Test
    @DisplayName("TC_2.4: Errore nel DAO")
    void testGetMentorCasuali_DAOError() throws SQLException {
        when(utenteDAO.getAllMentors()).thenThrow(new SQLException("Errore DB"));
        assertThrows(SQLException.class, () -> homeService.getMentorCasuali());
    }
}
