package sottosistemi.AreaUtente.service;

import model.beans.Sessione;
import model.dao.PrenotazioneDAO;
import model.dao.SessioneDAO;
import model.dto.PrenotazioneDetailsDTO;

import java.util.List;

/**
 * Service per la Dashboard Mentor,
 * delega a SessioneDAO e PrenotazioneDAO le operazioni di recupero sessioni e prenotazioni.
 */
public class MentorDashboardService {

    private final PrenotazioneDAO prenotazioneDAO;
    private final SessioneDAO sessioneDAO;

    public MentorDashboardService() {
        super();
        this.prenotazioneDAO = new PrenotazioneDAO();
        this.sessioneDAO = new SessioneDAO();
    }

    public MentorDashboardService (PrenotazioneDAO prenotazioneDAO, SessioneDAO sessioneDAO){
        this.prenotazioneDAO = prenotazioneDAO;
        this.sessioneDAO = sessioneDAO;
    }

    /**
     * Restituisce tutte le sessioni relative a un determinato mentor.
     *
     * @param mentorId l'ID del mentor
     * @return lista di Sessione
     * @throws Exception gestione di eventuali errori DAO
     */
    public List<Sessione> findSessionsByMentorId(int mentorId) throws Exception {
        return sessioneDAO.findByUserId(mentorId);
    }

    /**
     * Restituisce le prenotazioni attive (in formato dettagliato) per un mentor.
     *
     * @param mentorId l'ID del mentor
     * @return lista di PrenotazioneDetailsDTO attive per il mentor
     * @throws Exception gestione di eventuali errori DAO
     */
    public List<PrenotazioneDetailsDTO> findActiveBookingsForMentor(int mentorId) throws Exception {
        return prenotazioneDAO.findActiveDetailsByMentorId(mentorId);
    }

}
