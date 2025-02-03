package sottosistemi.Prenotazione.service;

import model.beans.Sessione;
import model.beans.Timeslot;
import model.dao.SessioneDAO;
import model.dao.TimeslotDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service che incapsula la logica di recupero Sessioni e relativi timeslot,
 * incluse le operazioni di ricerca e filtraggio.
 */
public class SessionRetrievalService {

    private final SessioneDAO sessioneDAO;
    private final TimeslotDAO timeslotDAO;
    public SessionRetrievalService(SessioneDAO sessioneDAO, TimeslotDAO timeslotDAO) {
        this.sessioneDAO = sessioneDAO;
        this.timeslotDAO = timeslotDAO;
    }

    public SessionRetrievalService() {
        this.sessioneDAO = new SessioneDAO();
        this.timeslotDAO = new TimeslotDAO();
    }

    public Sessione findSessionById(int sessioneId) throws SQLException {
        return sessioneDAO.doFindById(sessioneId);
    }


    public List<Timeslot> findTimeslotsBySessionId(int sessioneId) throws SQLException {
        return timeslotDAO.findBySessionId(sessioneId);
    }

    /**
     * Restituisce le sessioni "ATTIVA", escludendo la sessione corrente.
     * Limita il numero di risultati a 4 come nella logica originaria.
     */
    public List<Sessione> findCorrelatedSessions(int currentSessionId) throws SQLException {
        List<Sessione> allSessions = sessioneDAO.doFindAll();
        return allSessions.stream()
                .filter(s -> s.getIdSessione() != currentSessionId && "ATTIVA".equalsIgnoreCase(s.getStatusSessione()))
                .limit(4)
                .collect(Collectors.toList());
    }

    public List<Sessione> findSessionsByTitleLike(String query) throws SQLException {
        return sessioneDAO.findByTitleLike(query);
    }
}
