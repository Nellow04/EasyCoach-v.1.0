package sottosistemi.AreaUtente.service;

import model.beans.Utente;
import model.dao.PrenotazioneDAO;
import model.dao.UtenteDAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;

/**
 * Service per la gestione dell'account utente (rimozione, aggiornamento password, ecc.).
 */
public class AccountService {

    private final UtenteDAO utenteDAO;
    private final PrenotazioneDAO prenotazioneDAO;

    public AccountService() {
        super();
        this.utenteDAO = new UtenteDAO();
        this.prenotazioneDAO = new PrenotazioneDAO();
    }

    public AccountService (UtenteDAO utenteDAO, PrenotazioneDAO prenotazioneDAO){
        this.utenteDAO = utenteDAO;
        this.prenotazioneDAO = prenotazioneDAO;
    }

    /**
     * Verifica se l'utente ha prenotazioni attive.
     *
     * @param userId ID dell'utente
     * @return true se l'utente ha prenotazioni attive, false altrimenti
     * @throws SQLException in caso di errore DB
     */
    public boolean hasActiveBookingsForUser(int userId) throws SQLException {
        return prenotazioneDAO.hasActiveBookingsForUser(userId);
    }

    /**
     * Elimina un utente dal DB (doDelete).
     *
     * @param userId ID dell'utente da eliminare
     * @throws SQLException in caso di errore DB
     */
    public void deleteUser(int userId) throws SQLException {
        utenteDAO.doDelete(userId);
    }

    /**
     * Aggiorna la password dell'utente (doUpdatePassword).
     *
     * @param utente oggetto Utente già caricato
     * @throws Exception in caso di errore DB
     */
    public void updateUserPassword(Utente utente) throws Exception {
        utenteDAO.doUpdatePassword(utente);
    }

    /**
     * Hasha la password (SHA-256) in formato esadecimale.
     * Mantenuta la stessa logica di doppio hash, se la Servlet lo richiede.
     *
     * @param password la password in chiaro (o già parzialmente hashata, dipende dalla logica esterna)
     * @return la password hashata in esadecimale
     * @throws Exception in caso di errori di hashing
     */
    public String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
