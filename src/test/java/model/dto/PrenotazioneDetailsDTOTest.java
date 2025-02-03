package model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Test Cases for PrenotazioneDetailsDTO")
class PrenotazioneDetailsDTOTest {

    @Test
    @DisplayName("TC_1: Test costruttore di default")
    void testDefaultConstructor() {
        PrenotazioneDetailsDTO dto = new PrenotazioneDetailsDTO();
        assertNotNull(dto);
    }

    @Test
    @DisplayName("TC_2: Test setter e getter")
    void testSettersAndGetters() {
        PrenotazioneDetailsDTO dto = new PrenotazioneDetailsDTO();
        LocalDateTime data = LocalDateTime.now();

        dto.setIdPrenotazione(1);
        dto.setTitolo("Corso Java");
        dto.setMentorName("Dott. Rossi");
        dto.setMenteeName("Mario Bianchi");
        dto.setOrario("10:30");
        dto.setLinkVideoconferenza("https://meeting.com/123");
        dto.setDataPrenotazione(data);
        dto.setStatusPrenotazione("Confermata");

        assertEquals(1, dto.getIdPrenotazione());
        assertEquals("Corso Java", dto.getTitolo());
        assertEquals("Dott. Rossi", dto.getMentorName());
        assertEquals("Mario Bianchi", dto.getMenteeName());
        assertEquals("10:30", dto.getOrario());
        assertEquals("https://meeting.com/123", dto.getLinkVideoconferenza());
        assertEquals(data, dto.getDataPrenotazione());
        assertEquals("Confermata", dto.getStatusPrenotazione());
    }
}
