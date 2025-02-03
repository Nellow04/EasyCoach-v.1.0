package model.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Cases for Pagamento")
class PagamentoTest {

    @Test
    @DisplayName("TC_1: Test costruttore di default")
    void testDefaultConstructor() {
        Pagamento pagamento = new Pagamento();
        assertNotNull(pagamento);
    }

    @Test
    @DisplayName("TC_2: Test setter e getter")
    void testSettersAndGetters() {
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPagamento(1);
        pagamento.setIdPrenotazione(2);
        pagamento.setMetodoPagamento("Carta di Credito");
        pagamento.setTotalePagato(100.50);
        pagamento.setStatusPagamento("Completato");
        pagamento.setDataPagamento("2025-01-29");

        assertEquals(1, pagamento.getIdPagamento());
        assertEquals(2, pagamento.getIdPrenotazione());
        assertEquals("Carta di Credito", pagamento.getMetodoPagamento());
        assertEquals(100.50, pagamento.getTotalePagato());
        assertEquals("Completato", pagamento.getStatusPagamento());
        assertEquals("2025-01-29", pagamento.getDataPagamento());
    }

    @Test
    @DisplayName("TC_3: Test equals() con stesso oggetto")
    void testEquals_SameObject() {
        Pagamento pagamento = new Pagamento();
        assertEquals(pagamento, pagamento);
    }

    @Test
    @DisplayName("TC_4: Test equals() con oggetto null")
    void testEquals_NullObject() {
        Pagamento pagamento = new Pagamento();
        assertNotEquals(pagamento, null);
    }

    @Test
    @DisplayName("TC_5: Test equals() con classe diversa")
    void testEquals_DifferentClass() {
        Pagamento pagamento = new Pagamento();
        assertNotEquals(pagamento, "String");
    }

    @Test
    @DisplayName("TC_6: Test equals() con oggetti uguali")
    void testEquals_SameValues() {
        Pagamento pagamento1 = new Pagamento();
        Pagamento pagamento2 = new Pagamento();

        pagamento1.setIdPagamento(1);
        pagamento1.setIdPrenotazione(2);
        pagamento1.setMetodoPagamento("Carta di Credito");
        pagamento1.setTotalePagato(100.50);
        pagamento1.setStatusPagamento("Completato");
        pagamento1.setDataPagamento("2025-01-29");

        pagamento2.setIdPagamento(1);
        pagamento2.setIdPrenotazione(2);
        pagamento2.setMetodoPagamento("Carta di Credito");
        pagamento2.setTotalePagato(100.50);
        pagamento2.setStatusPagamento("Completato");
        pagamento2.setDataPagamento("2025-01-29");

        assertEquals(pagamento1, pagamento2);
    }

    @Test
    @DisplayName("TC_7: Test equals() con oggetti diversi")
    void testEquals_DifferentValues() {
        Pagamento pagamento1 = new Pagamento();
        pagamento1.setIdPagamento(1);
        Pagamento pagamento2 = new Pagamento();
        pagamento2.setIdPagamento(2);
        assertNotEquals(pagamento1, pagamento2);
    }

    @Test
    @DisplayName("TC_8: Test hashCode() con oggetti uguali")
    void testHashCode_SameValues() {
        Pagamento pagamento1 = new Pagamento();
        Pagamento pagamento2 = new Pagamento();

        pagamento1.setIdPagamento(1);
        pagamento1.setIdPrenotazione(2);
        pagamento1.setMetodoPagamento("Carta di Credito");
        pagamento1.setTotalePagato(100.50);
        pagamento1.setStatusPagamento("Completato");
        pagamento1.setDataPagamento("2025-01-29");

        pagamento2.setIdPagamento(1);
        pagamento2.setIdPrenotazione(2);
        pagamento2.setMetodoPagamento("Carta di Credito");
        pagamento2.setTotalePagato(100.50);
        pagamento2.setStatusPagamento("Completato");
        pagamento2.setDataPagamento("2025-01-29");

        assertEquals(pagamento1.hashCode(), pagamento2.hashCode());
    }

    @Test
    @DisplayName("TC_9: Test toString() output")
    void testToString() {
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPagamento(1);
        pagamento.setIdPrenotazione(2);
        pagamento.setMetodoPagamento("Carta di Credito");
        pagamento.setTotalePagato(100.50);
        pagamento.setStatusPagamento("Completato");
        pagamento.setDataPagamento("2025-01-29");

        String expected = "Pagamento{" +
                "idPagamento=1, idPrenotazione=2, metodoPagamento='Carta di Credito', totalePagato=100.5, statusPagamento='Completato', dataPagamento='2025-01-29'}";

        assertEquals(expected, pagamento.toString());
    }
}