package model.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Cases for Utente")
class UtenteTest {

    @Test
    @DisplayName("TC_1: Test costruttore di default")
    void testDefaultConstructor() {
        Utente utente = new Utente();
        assertNotNull(utente);
    }

    @Test
    @DisplayName("TC_2: Test setter e getter")
    void testSettersAndGetters() {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        utente.setEmail("test@email.com");
        utente.setNome("Mario");
        utente.setCognome("Rossi");
        utente.setPassword("password123");
        utente.setRuolo("Admin");

        assertEquals(1, utente.getIdUtente());
        assertEquals("test@email.com", utente.getEmail());
        assertEquals("Mario", utente.getNome());
        assertEquals("Rossi", utente.getCognome());
        assertEquals("password123", utente.getPassword());
        assertEquals("Admin", utente.getRuolo());
    }

    @Test
    @DisplayName("TC_3: Test equals() con stesso oggetto")
    void testEquals_SameObject() {
        Utente utente = new Utente();
        assertEquals(utente, utente);
    }

    @Test
    @DisplayName("TC_4: Test equals() con oggetto null")
    void testEquals_NullObject() {
        Utente utente = new Utente();
        assertNotEquals(utente, null);
    }

    @Test
    @DisplayName("TC_5: Test equals() con classe diversa")
    void testEquals_DifferentClass() {
        Utente utente = new Utente();
        assertNotEquals(utente, "String");
    }

    @Test
    @DisplayName("TC_6: Test equals() con oggetti uguali")
    void testEquals_SameValues() {
        Utente utente1 = new Utente();
        Utente utente2 = new Utente();

        utente1.setIdUtente(1);
        utente1.setEmail("test@email.com");
        utente1.setNome("Mario");
        utente1.setCognome("Rossi");
        utente1.setPassword("password123");
        utente1.setRuolo("Admin");

        utente2.setIdUtente(1);
        utente2.setEmail("test@email.com");
        utente2.setNome("Mario");
        utente2.setCognome("Rossi");
        utente2.setPassword("password123");
        utente2.setRuolo("Admin");

        assertEquals(utente1, utente2);
    }

    @Test
    @DisplayName("TC_7: Test equals() con oggetti diversi")
    void testEquals_DifferentValues() {
        Utente utente1 = new Utente();
        utente1.setIdUtente(1);
        Utente utente2 = new Utente();
        utente2.setIdUtente(2);
        assertNotEquals(utente1, utente2);
    }

    @Test
    @DisplayName("TC_8: Test hashCode() con oggetti uguali")
    void testHashCode_SameValues() {
        Utente utente1 = new Utente();
        Utente utente2 = new Utente();

        utente1.setIdUtente(1);
        utente1.setEmail("test@email.com");
        utente1.setNome("Mario");
        utente1.setCognome("Rossi");
        utente1.setPassword("password123");
        utente1.setRuolo("Admin");

        utente2.setIdUtente(1);
        utente2.setEmail("test@email.com");
        utente2.setNome("Mario");
        utente2.setCognome("Rossi");
        utente2.setPassword("password123");
        utente2.setRuolo("Admin");

        assertEquals(utente1.hashCode(), utente2.hashCode());
    }

    @Test
    @DisplayName("TC_9: Test toString() output")
    void testToString() {
        Utente utente = new Utente();
        utente.setIdUtente(1);
        utente.setEmail("test@email.com");
        utente.setNome("Mario");
        utente.setCognome("Rossi");
        utente.setPassword("password123");
        utente.setRuolo("Admin");

        String expected = "Utente{" +
                "idUtente=1, email='test@email.com', nome='Mario', cognome='Rossi', password='password123', ruolo='Admin'}";

        assertEquals(expected, utente.toString());
    }
}