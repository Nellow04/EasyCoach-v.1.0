package sottosistemi.AreaUtente.service;

import model.dao.PrenotazioneDAO;
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

@DisplayName("Test Cases for MenteeDashboardService")
class MenteeDashboardServiceTest {

    @Mock
    private PrenotazioneDAO prenotazioneDAO;
    @InjectMocks
    private MenteeDashboardService menteeDashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_3.1: Il mentee ha prenotazioni attive")
    void testFindActiveBookingsForMentee_WithBookings() throws Exception {
        when(prenotazioneDAO.findActiveDetailsByMenteeId(1)).thenReturn(List.of(new PrenotazioneDetailsDTO()));
        assertFalse(menteeDashboardService.findActiveBookingsForMentee(1).isEmpty());
    }

    @Test
    @DisplayName("TC_3.2: Il mentee non ha prenotazioni attive")
    void testFindActiveBookingsForMentee_NoBookings() throws Exception {
        when(prenotazioneDAO.findActiveDetailsByMenteeId(1)).thenReturn(List.of());
        assertTrue(menteeDashboardService.findActiveBookingsForMentee(1).isEmpty());
    }

    @Test
    @DisplayName("TC_3.3: Errore nel DAO")
    void testFindActiveBookingsForMentee_DAOError() throws Exception {
        when(prenotazioneDAO.findActiveDetailsByMenteeId(1)).thenThrow(new RuntimeException("Errore DB"));
        assertThrows(RuntimeException.class, () -> menteeDashboardService.findActiveBookingsForMentee(1));
    }

    @Test
    @DisplayName("TC_4.1: Il mentee ha prenotazioni concluse")
    void testFindCompletedBookingsForMentee_WithBookings() throws Exception {
        when(prenotazioneDAO.findCompletedDetailsByMenteeId(1)).thenReturn(List.of(new PrenotazioneDetailsDTO()));
        assertFalse(menteeDashboardService.findCompletedBookingsForMentee(1).isEmpty());
    }

    @Test
    @DisplayName("TC_4.2: Il mentee non ha prenotazioni concluse")
    void testFindCompletedBookingsForMentee_NoBookings() throws Exception {
        when(prenotazioneDAO.findCompletedDetailsByMenteeId(1)).thenReturn(List.of());
        assertTrue(menteeDashboardService.findCompletedBookingsForMentee(1).isEmpty());
    }

    @Test
    @DisplayName("TC_4.3: Errore nel DAO")
    void testFindCompletedBookingsForMentee_DAOError() throws Exception {
        when(prenotazioneDAO.findCompletedDetailsByMenteeId(1)).thenThrow(new RuntimeException("Errore DB"));
        assertThrows(RuntimeException.class, () -> menteeDashboardService.findCompletedBookingsForMentee(1));
    }
}