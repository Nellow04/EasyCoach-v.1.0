package sottosistemi.Sessione.service;

import model.beans.Sessione;
import model.beans.Timeslot;
import model.dao.PrenotazioneDAO;
import model.dao.SessioneDAO;
import model.dao.TimeslotDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service che gestisce la logica legata a Sessioni e Timeslot,
 * delegando l'accesso ai DAO corrispondenti.
 */
public class SessionManagementService {

    private final SessioneDAO sessioneDAO;
    private final TimeslotDAO timeslotDAO;
    private final PrenotazioneDAO prenotazioneDAO;

    public SessionManagementService() {
        super();
        this.sessioneDAO = new SessioneDAO();
        this.timeslotDAO = new TimeslotDAO();
        this.prenotazioneDAO = new PrenotazioneDAO();
    }

    public SessionManagementService (SessioneDAO sessioneDAO, TimeslotDAO timeslotDAO, PrenotazioneDAO prenotazioneDAO){
        this.sessioneDAO = sessioneDAO;
        this.timeslotDAO = timeslotDAO;
        this.prenotazioneDAO = prenotazioneDAO;
    }

    /**
     * Restituisce una lista di timeslot (giorno/orario) per un determinato mentor,
     * già mappati a coppie chiave-valore (giorno, orario).
     *
     * @param mentorId ID del mentor
     * @return lista di mappe con campi "giorno" e "orario"
     * @throws SQLException in caso di errori di accesso al DB
     */
    public List<Map<String, Integer>> getTimeslotsByMentorIdAsMap(int mentorId) throws SQLException {
        List<Timeslot> occupiedTimeslots = timeslotDAO.findByMentorId(mentorId);
        return occupiedTimeslots.stream()
                .map(ts -> Map.of(
                        "giorno", ts.getGiorno(),
                        "orario", ts.getOrario()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Crea una nuova sessione (inserendola nel DB) e relativi timeslot.
     * Ritorna l'idSessione generato.
     *
     * @param session  Bean Sessione con i campi base (idUtente, titolo, descrizione, prezzo, immagine, status)
     * @param days     array di giorni selezionati
     * @param hours    array di orari selezionati
     * @return l'ID della nuova sessione creata
     * @throws SQLException in caso di errori di accesso al DB
     */
    public int createSession(Sessione session, String[] days, String[] hours) throws SQLException {
        // Salva la sessione principale
        int idSessione = sessioneDAO.doSave(session);

        // Salva i timeslot associati
        if (days != null && hours != null && days.length == hours.length) {
            for (int i = 0; i < days.length; i++) {
                Timeslot timeslot = new Timeslot();
                timeslot.setIdSessione(idSessione);
                timeslot.setGiorno(Integer.parseInt(days[i]));
                timeslot.setOrario(Integer.parseInt(hours[i]));
                timeslotDAO.doSave(timeslot);
            }
        }
        return idSessione;
    }

    /**
     * Recupera una sessione tramite il suo ID.
     *
     * @param idSessione ID della sessione
     * @return la sessione corrispondente o null se non trovata
     * @throws SQLException in caso di errori di accesso al DB
     */
    public Sessione findSessionById(int idSessione) throws SQLException {
        return sessioneDAO.doFindById(idSessione);
    }

    /**
     * Recupera tutti i timeslot associati a una sessione.
     *
     * @param idSessione ID della sessione
     * @return lista di Timeslot
     * @throws SQLException in caso di errori di accesso al DB
     */
    public List<Timeslot> findTimeslotsBySessionId(int idSessione) throws SQLException {
        return timeslotDAO.findBySessionId(idSessione);
    }

    /**
     * Aggiorna i campi di una sessione e sostituisce i timeslot con i nuovi.
     *
     * @param session  Bean sessione già popolato con i nuovi dati
     * @param days     array di giorni selezionati
     * @param hours    array di orari selezionati
     * @throws SQLException in caso di errori di accesso al DB
     */
    public void updateSession(Sessione session, String[] days, String[] hours) throws SQLException {
        // Aggiorna i dati di Sessione
        sessioneDAO.doUpdate(session);

        // Rimuove i vecchi timeslot
        timeslotDAO.doDeleteBySessione(session.getIdSessione());

        // Crea i nuovi timeslot
        if (days != null && hours != null && days.length == hours.length) {
            for (int i = 0; i < days.length; i++) {
                Timeslot timeslot = new Timeslot();
                timeslot.setIdSessione(session.getIdSessione());
                timeslot.setGiorno(Integer.parseInt(days[i]));
                timeslot.setOrario(Integer.parseInt(hours[i]));
                timeslotDAO.doSave(timeslot);
            }
        }
    }

    /**
     * Verifica se esistono prenotazioni attive per una sessione.
     *
     * @param idSessione ID della sessione
     * @return true se ci sono prenotazioni attive, altrimenti false
     * @throws SQLException in caso di errori di accesso al DB
     */
    public boolean hasActiveBookings(int idSessione) throws SQLException {
        return prenotazioneDAO.hasActiveBookings(idSessione);
    }

    /**
     * Archivia una sessione (impostandone lo status a "ARCHIVIATA") ed elimina i suoi timeslot.
     *
     * @param session sessione da archiviare
     * @throws SQLException in caso di errori di accesso al DB
     */
    public void archiveSession(Sessione session) throws SQLException {
        session.setStatusSessione("ARCHIVIATA");
        sessioneDAO.doUpdate(session);

        // Elimina tutti i timeslot relativi
        timeslotDAO.doDeleteBySessione(session.getIdSessione());
    }
}
