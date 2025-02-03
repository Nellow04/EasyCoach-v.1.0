package model.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Cases for Prenotazione")
class PrenotazioneTest {

    @Test
    @DisplayName("TC_1: Test costruttore di default")
    void testDefaultConstructor() {
        Prenotazione prenotazione = new Prenotazione();
        assertNotNull(prenotazione);
    }

    @Test
    @DisplayName("TC_2: Test setter e getter")
    void testSettersAndGetters() {
        Prenotazione prenotazione = new Prenotazione();
        LocalDateTime data = LocalDateTime.now();

        prenotazione.setIdPrenotazione(1);
        prenotazione.setIdUtente(2);
        prenotazione.setIdTimeslot(3);
        prenotazione.setIdSessione(4);
        prenotazione.setDataPrenotazione(data);
        prenotazione.setLinkVideoconferenza("https://meeting.com/123");
        prenotazione.setStatusPrenotazione("Confermata");

        assertEquals(1, prenotazione.getIdPrenotazione());
        assertEquals(2, prenotazione.getIdUtente());
        assertEquals(3, prenotazione.getIdTimeslot());
        assertEquals(4, prenotazione.getIdSessione());
        assertEquals(data, prenotazione.getDataPrenotazione());
        assertEquals("https://meeting.com/123", prenotazione.getLinkVideoconferenza());
        assertEquals("Confermata", prenotazione.getStatusPrenotazione());
    }

    @Test
    @DisplayName("TC_3: Test equals() con stesso oggetto")
    void testEquals_SameObject() {
        Prenotazione prenotazione = new Prenotazione();
        assertEquals(prenotazione, prenotazione);
    }

    @Test
    @DisplayName("TC_4: Test equals() con oggetto null")
    void testEquals_NullObject() {
        Prenotazione prenotazione = new Prenotazione();
        assertNotEquals(prenotazione, null);
    }

    @Test
    @DisplayName("TC_5: Test equals() con classe diversa")
    void testEquals_DifferentClass() {
        Prenotazione prenotazione = new Prenotazione();
        assertNotEquals(prenotazione, "String");
    }

    @Test
    @DisplayName("TC_6: Test equals() con oggetti uguali")
    void testEquals_SameValues() {
        Prenotazione prenotazione1 = new Prenotazione();
        Prenotazione prenotazione2 = new Prenotazione();
        LocalDateTime data = LocalDateTime.now();

        prenotazione1.setIdPrenotazione(1);
        prenotazione1.setIdUtente(2);
        prenotazione1.setIdTimeslot(3);
        prenotazione1.setIdSessione(4);
        prenotazione1.setDataPrenotazione(data);
        prenotazione1.setLinkVideoconferenza("https://meeting.com/123");
        prenotazione1.setStatusPrenotazione("Confermata");

        prenotazione2.setIdPrenotazione(1);
        prenotazione2.setIdUtente(2);
        prenotazione2.setIdTimeslot(3);
        prenotazione2.setIdSessione(4);
        prenotazione2.setDataPrenotazione(data);
        prenotazione2.setLinkVideoconferenza("https://meeting.com/123");
        prenotazione2.setStatusPrenotazione("Confermata");

        assertEquals(prenotazione1, prenotazione2);
    }

    @Test
    @DisplayName("TC_7: Test equals() con oggetti diversi")
    void testEquals_DifferentValues() {
        Prenotazione prenotazione1 = new Prenotazione();
        prenotazione1.setIdPrenotazione(1);
        Prenotazione prenotazione2 = new Prenotazione();
        prenotazione2.setIdPrenotazione(2);
        assertNotEquals(prenotazione1, prenotazione2);
    }

    @Test
    @DisplayName("TC_8: Test hashCode() con oggetti uguali")
    void testHashCode_SameValues() {
        Prenotazione prenotazione1 = new Prenotazione();
        Prenotazione prenotazione2 = new Prenotazione();
        LocalDateTime data = LocalDateTime.now();

        prenotazione1.setIdPrenotazione(1);
        prenotazione1.setIdUtente(2);
        prenotazione1.setIdTimeslot(3);
        prenotazione1.setIdSessione(4);
        prenotazione1.setDataPrenotazione(data);
        prenotazione1.setLinkVideoconferenza("https://meeting.com/123");
        prenotazione1.setStatusPrenotazione("Confermata");

        prenotazione2.setIdPrenotazione(1);
        prenotazione2.setIdUtente(2);
        prenotazione2.setIdTimeslot(3);
        prenotazione2.setIdSessione(4);
        prenotazione2.setDataPrenotazione(data);
        prenotazione2.setLinkVideoconferenza("https://meeting.com/123");
        prenotazione2.setStatusPrenotazione("Confermata");

        assertEquals(prenotazione1.hashCode(), prenotazione2.hashCode());
    }

    @Test
    @DisplayName("TC_9: Test toString() output")
    void testToString() {
        Prenotazione prenotazione = new Prenotazione();
        LocalDateTime data = LocalDateTime.now();
        prenotazione.setIdPrenotazione(1);
        prenotazione.setIdUtente(2);
        prenotazione.setIdTimeslot(3);
        prenotazione.setIdSessione(4);
        prenotazione.setDataPrenotazione(data);
        prenotazione.setLinkVideoconferenza("https://meeting.com/123");
        prenotazione.setStatusPrenotazione("Confermata");

        String expected = "Prenotazione{" +
                "idPrenotazione=1, idUtente=2, idTimeslot=3, idSessione=4, dataPrenotazione=" + data +
                ", linkVideoconferenza='https://meeting.com/123', statusPrenotazione='Confermata'}";

        assertEquals(expected, prenotazione.toString());
    }
}