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
 * Service dedicato alla gestione degli utenti e relative operazioni.
 */
public class AdminUserManagementService {

    private final UtenteDAO utenteDAO;
    private final SessioneDAO sessioneDAO;

    public AdminUserManagementService() {
        super();
        this.utenteDAO = new UtenteDAO();
        this.sessioneDAO = new SessioneDAO();
    }

    public AdminUserManagementService (UtenteDAO utenteDAO, SessioneDAO sessioneDAO){
        this.utenteDAO = utenteDAO;
        this.sessioneDAO = sessioneDAO;
    }

    /**
     * Recupera tutti gli utenti dal DB e ne produce una lista di mappe
     * semplificate con i campi di interesse (id, nome completo, email, ruolo).
     *
     * @return lista di mappe con informazioni semplificate degli utenti
     * @throws Exception in caso di errori di accesso al DB
     */
    public List<Map<String, Object>> getAllUsersSimplified() throws Exception {
        List<Utente> users = utenteDAO.doFindAll();

        // Converto la lista di utenti in una lista di mappe semplificate
        return users.stream()
                .map(user -> {
                    Map<String, Object> simplified = new HashMap<>();
                    simplified.put("id", user.getIdUtente());
                    simplified.put("nome", user.getNome() + " " + user.getCognome());
                    simplified.put("email", user.getEmail());
                    simplified.put("ruolo", user.getRuolo());
                    return simplified;
                })
                .collect(Collectors.toList());
    }

    /**
     * Elimina un utente dal DB, archiviando prima tutte le sessioni (come da logica originaria).
     *
     * @param userId ID dell'utente da eliminare
     * @throws Exception in caso di errori di accesso al DB
     */
    public void deleteUser(int userId) throws Exception {
        // Nella logica originale vengono archiviate tutte le sessioni (non solo quelle dell'utente),
        // quindi manteniamo esattamente lo stesso comportamento.
        List<Sessione> sessioniUtente = sessioneDAO.doFindAll();
        for (Sessione sessione : sessioniUtente) {
            sessioneDAO.archiveSession(sessione.getIdSessione(), true);
        }

        // Effettiva eliminazione dell'utente
        utenteDAO.doDelete(userId);
    }
}