package sottosistemi.Sessione.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionValidationService Tests")
class SessionValidationServiceTest {

    private SessionValidationService service;

    @BeforeEach
    void setUp() {
        service = new SessionValidationService();
    }

    @Test
    @DisplayName("TC_1.1: Titolo valido (2-25 caratteri)")
    void testValidateTitle_Valid() {
        assertTrue(service.validateTitle("Titolo valido"));
    }

    @Test
    @DisplayName("TC_1.2: Titolo nullo")
    void testValidateTitle_Null() {
        assertFalse(service.validateTitle(null));
    }

    @Test
    @DisplayName("TC_1.3: Titolo troppo corto (< 2 caratteri)")
    void testValidateTitle_TooShort() {
        assertFalse(service.validateTitle("A"));
    }

    @Test
    @DisplayName("TC_1.4: Titolo troppo lungo (> 25 caratteri)")
    void testValidateTitle_TooLong() {
        assertFalse(service.validateTitle("Questo titolo è decisamente troppo lungo"));
    }

    @Test
    @DisplayName("TC_2.1: Descrizione valida (2-250 caratteri)")
    void testValidateDescription_Valid() {
        assertTrue(service.validateDescription("Descrizione valida"));
    }

    @Test
    @DisplayName("TC_2.2: Descrizione nulla")
    void testValidateDescription_Null() {
        assertFalse(service.validateDescription(null));
    }

    @Test
    @DisplayName("TC_2.3: Descrizione troppo corta (< 2 caratteri)")
    void testValidateDescription_TooShort() {
        assertFalse(service.validateDescription("A"));
    }

    @Test
    @DisplayName("TC_2.4: Descrizione troppo lunga (> 250 caratteri)")
    void testValidateDescription_TooLong() {
        String longDescription = "A".repeat(251);
        assertFalse(service.validateDescription(longDescription));
    }

    @Test
    @DisplayName("TC_3.1: Prezzo valido")
    void testValidatePrice_Valid() {
        assertTrue(service.validatePrice("123.45"));
    }

    @Test
    @DisplayName("TC_3.2: Prezzo nullo")
    void testValidatePrice_Null() {
        assertFalse(service.validatePrice(null));
    }

    @Test
    @DisplayName("TC_3.3: Prezzo non numerico")
    void testValidatePrice_NonNumeric() {
        assertFalse(service.validatePrice("abc"));
    }

    @Test
    @DisplayName("TC_3.4: Prezzo fuori range (> 999)")
    void testValidatePrice_TooHigh() {
        assertFalse(service.validatePrice("1000"));
    }

    @Test
    @DisplayName("TC_3.5: Prezzo negativo o zero")
    void testValidatePrice_NegativeOrZero() {
        assertFalse(service.validatePrice("0"));
        assertFalse(service.validatePrice("-10"));
    }

    @Test
    @DisplayName("TC_4.1: Timeslots validi")
    void testValidateTimeslots_Valid() {
        String[] days = {"1", "2"};
        String[] hours = {"10", "15"};
        assertTrue(service.validateTimeslots(days, hours));
    }

    @Test
    @DisplayName("TC_4.2: Timeslots nulli")
    void testValidateTimeslots_Null() {
        assertFalse(service.validateTimeslots(null, null));
    }

    @Test
    @DisplayName("TC_4.3: Timeslots con lunghezze diverse")
    void testValidateTimeslots_DifferentLengths() {
        String[] days = {"1", "2"};
        String[] hours = {"10"};
        assertFalse(service.validateTimeslots(days, hours));
    }

    @Test
    @DisplayName("TC_4.4: Timeslots con valori non validi")
    void testValidateTimeslots_InvalidValues() {
        String[] days = {"1", "7"}; // 7 non è un giorno valido
        String[] hours = {"10", "24"}; // 24 non è un'ora valida
        assertFalse(service.validateTimeslots(days, hours));
    }

    @Test
    @DisplayName("TC_6.1: Validazione completa con dati validi")
    void testValidateForm_Valid() {
        String[] days = {"1", "2"};
        String[] hours = {"10", "15"};
        Map<String, String> errors = service.validateForm("Titolo", "Descrizione", "123.45", days, hours, true);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("TC_6.2: Validazione completa con errori")
    void testValidateForm_Errors() {
        String[] days = {"1", "7"}; // Giorno non valido
        String[] hours = {"10"};
        Map<String, String> errors = service.validateForm("T", "D", "-10", days, hours, false);

        assertEquals(5, errors.size());
        assertTrue(errors.containsKey("titolo"));
        assertTrue(errors.containsKey("descrizione"));
        assertTrue(errors.containsKey("prezzo"));
        assertTrue(errors.containsKey("timeslots"));
        assertTrue(errors.containsKey("immagine"));
    }



    @Test
    @DisplayName("TC_6.3: Validazione Form Edit con errori")
    void testValidateFormEdit_WithErrors() {
        String[] days = {"7"}; // Giorno non valido
        String[] hours = {"24"}; // Ora non valida
        Map<String, String> errors = service.validateFormEdit("A", "B", "-10", days, hours);

        assertEquals(4, errors.size());
        assertTrue(errors.containsKey("nome")); // Errore nel titolo
        assertTrue(errors.containsKey("descrizione")); // Errore nella descrizione
        assertTrue(errors.containsKey("prezzo")); // Errore nel prezzo
        assertTrue(errors.containsKey("timeslots")); // Errore nei timeslots
    }

    @Test
    @DisplayName("TC_6.4: Validazione Form Edit senza errori")
    void testValidateFormEdit_NoErrors() {
        String[] days = {"1", "2"};
        String[] hours = {"10", "15"};
        Map<String, String> errors = service.validateFormEdit("Titolo", "Descrizione valida", "123.45", days, hours);

        assertTrue(errors.isEmpty()); // Nessun errore
    }

}
