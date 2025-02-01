package sottosistemi.Prenotazione.service;

import model.beans.Prenotazione;
import model.dao.PrenotazioneDAO;
import model.dao.TimeslotDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service per la gestione delle Prenotazioni
 */
public class BookingService {

    private final PrenotazioneDAO prenotazioneDAO;
    private final TimeslotDAO timeslotDAO;

    public BookingService(PrenotazioneDAO prenotazioneDAO, TimeslotDAO timeslotDAO) {
        this.prenotazioneDAO = prenotazioneDAO;
        this.timeslotDAO = timeslotDAO;
    }

    public BookingService() {
        this.prenotazioneDAO = new PrenotazioneDAO();
        this.timeslotDAO = new TimeslotDAO();
    }

    /**
     * checkAvailability: prende la Map di parametri, effettua parse e controlla la disponibilità
     */
    public Map<String, Object> checkAvailability(Map<String, String> params) throws SQLException {
        Map<String, Object> result = new HashMap<>();

        // Estrai parametri
        String timeslotIdStr = params.get("timeslotId");
        String dateStr = params.get("date");

        // Validazione di base
        if (timeslotIdStr == null || dateStr == null) {
            result.put("error", "Parametri mancanti per checkAvailability");
            return result;
        }

        int timeslotId = Integer.parseInt(timeslotIdStr);
        LocalDateTime dateTime = LocalDate.parse(dateStr).atStartOfDay();

        // Richiama il metodo DAO che già avevi
        Map<String, Object> availability = prenotazioneDAO.checkTimeslotStatus(timeslotId, dateTime);
        // Ritorna i risultati
        // availability contiene "disponibile" (boolean) e, se presente, "status"
        return availability;
    }

    /**
     * createBooking: presa la mappa dei parametri, effettua tutti i controlli,
     *               verifica la disponibilità e crea la prenotazione
     */
    public Prenotazione createBooking(Map<String, String> params) throws SQLException {
        // Estrai i parametri
        String timeslotIdStr = params.get("timeslotId");
        String dateStr       = params.get("dataPrenotazione");
        String sessioneIdStr = params.get("idSessione");
        String userIdStr     = params.get("idUtente");

        // Controlla che non siano null
        if (timeslotIdStr == null || dateStr == null || sessioneIdStr == null || userIdStr == null) {
            throw new IllegalArgumentException("Parametri mancanti per la creazione della prenotazione");
        }

        int timeslotId  = Integer.parseInt(timeslotIdStr);
        int sessioneId  = Integer.parseInt(sessioneIdStr);
        int userId      = Integer.parseInt(userIdStr);
        LocalDateTime bookingDateTime = LocalDate.parse(dateStr).atStartOfDay();

        // Verifica disponibilità
        if (!isTimeslotDisponibile(timeslotId, bookingDateTime)) {
            throw new IllegalStateException("Il timeslot selezionato non è più disponibile");
        }

        // Se disponibile, crea e salva la prenotazione
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setIdUtente(userId);
        prenotazione.setIdTimeslot(timeslotId);
        prenotazione.setIdSessione(sessioneId);
        prenotazione.setDataPrenotazione(bookingDateTime);
        prenotazione.setStatusPrenotazione("IN_ATTESA");

        prenotazioneDAO.doSave(prenotazione);
        return prenotazione;
    }

    /**
     * Conferma la prenotazione (stato "CONCLUSA") partendo dalla mappa dei parametri
     */
    public Prenotazione confirmBooking(Map<String, String> params) throws SQLException {
        String idPrenotazioneStr = params.get("idPrenotazione");
        if (idPrenotazioneStr == null) {
            throw new IllegalArgumentException("ID prenotazione mancante");
        }

        int idPrenotazione = Integer.parseInt(idPrenotazioneStr);

        // Usa il metodo esistente
        Prenotazione prenotazione = prenotazioneDAO.doFindById(idPrenotazione);
        if (prenotazione == null) {
            return null;
        }

        prenotazione.setStatusPrenotazione("CONCLUSA");
        prenotazioneDAO.doUpdate(prenotazione);
        return prenotazione;
    }


    // ============= Metodi già esistenti sotto ============= //

    /**
     * Controlla la disponibilità di un timeslot nella data/ora specifica
     */
    public boolean isTimeslotDisponibile(int timeslotId, LocalDateTime dateTime) throws SQLException {
        return prenotazioneDAO.isDisponibile(timeslotId, dateTime);
    }

    /**
     * Usato internamente per la logica di checkTimeslotStatus,
     * ma ora lo spostiamo in un metodo un po' più completo (vedi sopra).
     */

    /*public Map<String, Object> checkTimeslotStatus(int timeslotId, LocalDateTime dateTime) throws SQLException {
        return prenotazioneDAO.checkTimeslotStatus(timeslotId, dateTime);
    }*/

    // ...
}
