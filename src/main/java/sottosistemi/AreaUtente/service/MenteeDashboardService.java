package sottosistemi.AreaUtente.service;

import model.dao.PrenotazioneDAO;
import model.dto.PrenotazioneDetailsDTO;

import java.util.List;

/**
 * Service per la Dashboard Mentee,
 * delega a PrenotazioneDAO le operazioni di recupero prenotazioni.
 */
public class MenteeDashboardService {

    private final PrenotazioneDAO prenotazioneDAO;

    public MenteeDashboardService() {
        super();
        this.prenotazioneDAO = new PrenotazioneDAO();
    }

    public MenteeDashboardService(PrenotazioneDAO prenotazioneDAO) {
        this.prenotazioneDAO = prenotazioneDAO;
    }

    /**
     * Restituisce le prenotazioni attive per uno specifico mentee.
     *
     * @param menteeId l'ID del mentee
     * @return lista di PrenotazioneDetailsDTO attive
     * @throws Exception gestione di eventuali errori DAO
     */
    public List<PrenotazioneDetailsDTO> findActiveBookingsForMentee(int menteeId) throws Exception {
        return prenotazioneDAO.findActiveDetailsByMenteeId(menteeId);
    }

    /**
     * Restituisce le prenotazioni concluse per uno specifico mentee.
     *
     * @param menteeId l'ID del mentee
     * @return lista di PrenotazioneDetailsDTO concluse
     * @throws Exception gestione di eventuali errori DAO
     */
    public List<PrenotazioneDetailsDTO> findCompletedBookingsForMentee(int menteeId) throws Exception {
        return prenotazioneDAO.findCompletedDetailsByMenteeId(menteeId);
    }

}
