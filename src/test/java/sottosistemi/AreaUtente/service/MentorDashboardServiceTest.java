package sottosistemi.AreaUtente.service;

import model.beans.Sessione;
import model.dao.PrenotazioneDAO;
import model.dao.SessioneDAO;
import model.dto.PrenotazioneDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for MentorDashboardService")
class MentorDashboardServiceTest {

    @Mock
    private PrenotazioneDAO prenotazioneDAO;

    @Mock
    private SessioneDAO sessioneDAO;

    @InjectMocks
    private MentorDashboardService mentorDashboardService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_1.1: Il mentor ha sessioni")
    void testFindSessionsByMentorId_WithSessions() throws Exception {
        when(sessioneDAO.findByUserId(1)).thenReturn(List.of(new Sessione()));
        assertFalse(mentorDashboardService.findSessionsByMentorId(1).isEmpty());
    }

    @Test
    @DisplayName("TC_1.2: Il mentor non ha sessioni")
    void testFindSessionsByMentorId_NoSessions() throws Exception {
        when(sessioneDAO.findByUserId(1)).thenReturn(List.of());
        assertTrue(mentorDashboardService.findSessionsByMentorId(1).isEmpty());
    }

    @Test
    @DisplayName("TC_1.3: Errore nel DAO")
    void testFindSessionsByMentorId_DAOError() throws Exception {
        when(sessioneDAO.findByUserId(1)).thenThrow(new RuntimeException("Errore DB"));
        assertThrows(RuntimeException.class, () -> mentorDashboardService.findSessionsByMentorId(1));
    }

    @Test
    @DisplayName("TC_2.1: Il mentor ha prenotazioni attive")
    void testFindActiveBookingsForMentor_WithBookings() throws Exception {
        when(prenotazioneDAO.findActiveDetailsByMentorId(1)).thenReturn(List.of(new PrenotazioneDetailsDTO()));
        assertFalse(mentorDashboardService.findActiveBookingsForMentor(1).isEmpty());
    }

    @Test
    @DisplayName("TC_2.2: Il mentor non ha prenotazioni attive")
    void testFindActiveBookingsForMentor_NoBookings() throws Exception {
        when(prenotazioneDAO.findActiveDetailsByMentorId(1)).thenReturn(List.of());
        assertTrue(mentorDashboardService.findActiveBookingsForMentor(1).isEmpty());
    }

    @Test
    @DisplayName("TC_2.3: Errore nel DAO")
    void testFindActiveBookingsForMentor_DAOError() throws Exception {
        when(prenotazioneDAO.findActiveDetailsByMentorId(1)).thenThrow(new RuntimeException("Errore DB"));
        assertThrows(RuntimeException.class, () -> mentorDashboardService.findActiveBookingsForMentor(1));
    }
}