package model.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Cases for Timeslot")
class TimeslotTest {

    @Test
    @DisplayName("TC_1: Test costruttore di default")
    void testDefaultConstructor() {
        Timeslot timeslot = new Timeslot();
        assertNotNull(timeslot);
    }

    @Test
    @DisplayName("TC_2: Test setter e getter")
    void testSettersAndGetters() {
        Timeslot timeslot = new Timeslot();
        timeslot.setIdTimeslot(1);
        timeslot.setIdSessione(2);
        timeslot.setGiorno(3);
        timeslot.setOrario(10);

        assertEquals(1, timeslot.getIdTimeslot());
        assertEquals(2, timeslot.getIdSessione());
        assertEquals(3, timeslot.getGiorno());
        assertEquals(10, timeslot.getOrario());
    }

    @Test
    @DisplayName("TC_3: Test equals() con stesso oggetto")
    void testEquals_SameObject() {
        Timeslot timeslot = new Timeslot();
        assertEquals(timeslot, timeslot);
    }

    @Test
    @DisplayName("TC_4: Test equals() con oggetto null")
    void testEquals_NullObject() {
        Timeslot timeslot = new Timeslot();
        assertNotEquals(timeslot, null);
    }

    @Test
    @DisplayName("TC_5: Test equals() con classe diversa")
    void testEquals_DifferentClass() {
        Timeslot timeslot = new Timeslot();
        assertNotEquals(timeslot, "String");
    }

    @Test
    @DisplayName("TC_6: Test equals() con oggetti uguali")
    void testEquals_SameValues() {
        Timeslot timeslot1 = new Timeslot();
        Timeslot timeslot2 = new Timeslot();

        timeslot1.setIdTimeslot(1);
        timeslot1.setIdSessione(2);
        timeslot1.setGiorno(3);
        timeslot1.setOrario(10);

        timeslot2.setIdTimeslot(1);
        timeslot2.setIdSessione(2);
        timeslot2.setGiorno(3);
        timeslot2.setOrario(10);

        assertEquals(timeslot1, timeslot2);
    }

    @Test
    @DisplayName("TC_7: Test equals() con oggetti diversi")
    void testEquals_DifferentValues() {
        Timeslot timeslot1 = new Timeslot();
        timeslot1.setIdTimeslot(1);
        Timeslot timeslot2 = new Timeslot();
        timeslot2.setIdTimeslot(2);
        assertNotEquals(timeslot1, timeslot2);
    }

    @Test
    @DisplayName("TC_8: Test hashCode() con oggetti uguali")
    void testHashCode_SameValues() {
        Timeslot timeslot1 = new Timeslot();
        Timeslot timeslot2 = new Timeslot();

        timeslot1.setIdTimeslot(1);
        timeslot1.setIdSessione(2);
        timeslot1.setGiorno(3);
        timeslot1.setOrario(10);

        timeslot2.setIdTimeslot(1);
        timeslot2.setIdSessione(2);
        timeslot2.setGiorno(3);
        timeslot2.setOrario(10);

        assertEquals(timeslot1.hashCode(), timeslot2.hashCode());
    }

    @Test
    @DisplayName("TC_9: Test toString() output")
    void testToString() {
        Timeslot timeslot = new Timeslot();
        timeslot.setIdTimeslot(1);
        timeslot.setIdSessione(2);
        timeslot.setGiorno(3);
        timeslot.setOrario(10);

        String expected = "Timeslot{" +
                "idTimeslot=1, idSessione=2, giorno=3, orario=10}";

        assertEquals(expected, timeslot.toString());
    }
}