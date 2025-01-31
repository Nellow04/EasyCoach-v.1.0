package sottosistemi.Admin.service;

import model.dao.SessioneDAO;
import model.dao.UtenteDAO;
import model.beans.Sessione;
import model.beans.Utente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service dedicato alla gestione delle sessioni e relative operazioni.
 */
public class AdminSessionManagementService {

    private final SessioneDAO sessioneDAO;
    private final UtenteDAO utenteDAO;

    public AdminSessionManagementService() {
        super();
        this.sessioneDAO = new SessioneDAO();
        this.utenteDAO = new UtenteDAO();
    }

    public AdminSessionManagementService (SessioneDAO sessioneDAO, UtenteDAO utenteDAO){
        this.sessioneDAO = sessioneDAO;
        this.utenteDAO = utenteDAO;
    }

    /**
     * Recupera tutte le sessioni dal DB e arricchisce ciascuna con il nome del mentor (se possibile).
     *
     * @return lista di mappe con informazioni arricchite delle sessioni
     * @throws Exception in caso di errori di accesso al DB
     */
    public List<Map<String, Object>> getAllSessionsEnriched() throws Exception {
        List<Sessione> sessions = sessioneDAO.doFindAll();

        return sessions.stream()
                .map(session -> {
                    Map<String, Object> enriched = new HashMap<>();
                    enriched.put("idSessione", session.getIdSessione());
                    enriched.put("titolo", session.getTitolo());
                    enriched.put("descrizione", session.getDescrizione());
                    enriched.put("prezzo", session.getPrezzo());
                    enriched.put("statusSessione", session.getStatusSessione());
                    enriched.put("immagine", session.getImmagine());

                    try {
                        Utente mentor = utenteDAO.doFindById(session.getIdUtente());
                        if (mentor != null) {
                            enriched.put("mentorNome", mentor.getNome() + " " + mentor.getCognome());
                        } else {
                            enriched.put("mentorNome", "N/A");
                        }
                    } catch (Exception e) {
                        // Manteniamo la stessa gestione di eccezione
                        // (nella Servlet originale viene stampato l'errore e messo "N/A").
                        enriched.put("mentorNome", "N/A");
                    }

                    return enriched;
                })
                .collect(Collectors.toList());
    }

    /**
     * Archivia (logicamente) una sessione tramite la chiamata a archiveSession.
     *
     * @param sessionId ID della sessione da archiviare
     * @throws Exception in caso di errori di accesso al DB
     */
    public void deleteSession(int sessionId) throws Exception {
        // Viene passato true perché siamo nella AdminServlet, come da logica originale
        sessioneDAO.archiveSession(sessionId, true);
    }
}
