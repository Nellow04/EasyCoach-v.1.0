package sottosistemi.AreaUtente.service;

import model.beans.Sessione;
import model.beans.Utente;
import model.dao.SessioneDAO;
import model.dao.UtenteDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service per la gestione della logica della homepage.
 * Responsabile del recupero dei dati necessari per la visualizzazione
 * delle sessioni e dei mentor piÃ¹ attivi.
 */
public class HomeService {

    private final SessioneDAO sessioneDAO;
    private final UtenteDAO utenteDAO;

    public HomeService() {
        super();
        this.sessioneDAO = new SessioneDAO();
        this.utenteDAO = new UtenteDAO();
    }

    public HomeService(SessioneDAO sessioneDAO, UtenteDAO utenteDAO){
        this.sessioneDAO = sessioneDAO;
        this.utenteDAO = utenteDAO;
    }

    /**
     * Recupera una lista di tre sessioni casuali da visualizzare in evidenza nella homepage.
     *
     * @return lista di tre sessioni
     * @throws SQLException in caso di errori di accesso al database
     */
    public List<Sessione> getSessioniInEvidenza() throws SQLException {
        return sessioneDAO.getTreSessioniCasuali();
    }

    /**
     * Recupera una lista di mentor casuali (massimo tre) per la visualizzazione nella homepage.
     *
     * @return lista di tre mentor casuali o meno di tre se disponibili
     * @throws SQLException in caso di errori di accesso al database
     */
    public List<Utente> getMentorCasuali() throws SQLException {
        List<Utente> tuttiMentor = utenteDAO.getAllMentors();

        if (tuttiMentor.size() <= 3) {
            return new ArrayList<>(tuttiMentor);
        }

        // Mescola i mentor e restituisce i primi tre
        List<Utente> mentorMescolati = new ArrayList<>(tuttiMentor);
        Collections.shuffle(mentorMescolati);
        return mentorMescolati.subList(0, 3);
    }
}