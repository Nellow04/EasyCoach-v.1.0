package model.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Cases for Sessione")
class SessioneTest {

    @Test
    @DisplayName("TC_1: Test costruttore di default")
    void testDefaultConstructor() {
        Sessione sessione = new Sessione();
        assertNotNull(sessione);
    }

    @Test
    @DisplayName("TC_2: Test setter e getter")
    void testSettersAndGetters() {
        Sessione sessione = new Sessione();
        sessione.setIdSessione(1);
        sessione.setIdUtente(2);
        sessione.setTitolo("Corso Java");
        sessione.setDescrizione("Corso avanzato di Java");
        sessione.setPrezzo(99.99);
        sessione.setImmagine("java.jpg");
        sessione.setStatusSessione("Attivo");

        assertEquals(1, sessione.getIdSessione());
        assertEquals(2, sessione.getIdUtente());
        assertEquals("Corso Java", sessione.getTitolo());
        assertEquals("Corso avanzato di Java", sessione.getDescrizione());
        assertEquals(99.99, sessione.getPrezzo());
        assertEquals("java.jpg", sessione.getImmagine());
        assertEquals("Attivo", sessione.getStatusSessione());
    }

    @Test
    @DisplayName("TC_3: Test equals() con stesso oggetto")
    void testEquals_SameObject() {
        Sessione sessione = new Sessione();
        assertEquals(sessione, sessione);
    }

    @Test
    @DisplayName("TC_4: Test equals() con oggetto null")
    void testEquals_NullObject() {
        Sessione sessione = new Sessione();
        assertNotEquals(sessione, null);
    }

    @Test
    @DisplayName("TC_5: Test equals() con classe diversa")
    void testEquals_DifferentClass() {
        Sessione sessione = new Sessione();
        assertNotEquals(sessione, "String");
    }

    @Test
    @DisplayName("TC_6: Test equals() con oggetti uguali")
    void testEquals_SameValues() {
        Sessione sessione1 = new Sessione();
        Sessione sessione2 = new Sessione();

        sessione1.setIdSessione(1);
        sessione1.setIdUtente(2);
        sessione1.setTitolo("Corso Java");
        sessione1.setDescrizione("Corso avanzato di Java");
        sessione1.setPrezzo(99.99);
        sessione1.setImmagine("java.jpg");
        sessione1.setStatusSessione("Attivo");

        sessione2.setIdSessione(1);
        sessione2.setIdUtente(2);
        sessione2.setTitolo("Corso Java");
        sessione2.setDescrizione("Corso avanzato di Java");
        sessione2.setPrezzo(99.99);
        sessione2.setImmagine("java.jpg");
        sessione2.setStatusSessione("Attivo");

        assertEquals(sessione1, sessione2);
    }

    @Test
    @DisplayName("TC_7: Test equals() con oggetti diversi")
    void testEquals_DifferentValues() {
        Sessione sessione1 = new Sessione();
        sessione1.setIdSessione(1);
        Sessione sessione2 = new Sessione();
        sessione2.setIdSessione(2);
        assertNotEquals(sessione1, sessione2);
    }

    @Test
    @DisplayName("TC_8: Test hashCode() con oggetti uguali")
    void testHashCode_SameValues() {
        Sessione sessione1 = new Sessione();
        Sessione sessione2 = new Sessione();

        sessione1.setIdSessione(1);
        sessione1.setIdUtente(2);
        sessione1.setTitolo("Corso Java");
        sessione1.setDescrizione("Corso avanzato di Java");
        sessione1.setPrezzo(99.99);
        sessione1.setImmagine("java.jpg");
        sessione1.setStatusSessione("Attivo");

        sessione2.setIdSessione(1);
        sessione2.setIdUtente(2);
        sessione2.setTitolo("Corso Java");
        sessione2.setDescrizione("Corso avanzato di Java");
        sessione2.setPrezzo(99.99);
        sessione2.setImmagine("java.jpg");
        sessione2.setStatusSessione("Attivo");

        assertEquals(sessione1.hashCode(), sessione2.hashCode());
    }

    @Test
    @DisplayName("TC_9: Test toString() output")
    void testToString() {
        Sessione sessione = new Sessione();
        sessione.setIdSessione(1);
        sessione.setIdUtente(2);
        sessione.setTitolo("Corso Java");
        sessione.setDescrizione("Corso avanzato di Java");
        sessione.setPrezzo(99.99);
        sessione.setImmagine("java.jpg");
        sessione.setStatusSessione("Attivo");

        String expected = "Sessione{" +
                "idSessione=1, idUtente=2, titolo='Corso Java', descrizione='Corso avanzato di Java', prezzo=99.99, immagine='java.jpg', statusSessione='Attivo'}";

        assertEquals(expected, sessione.toString());
    }
}