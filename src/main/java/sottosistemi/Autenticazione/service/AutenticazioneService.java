package sottosistemi.Autenticazione.service;

import model.beans.Utente;
import model.dao.UtenteDAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AutenticazioneService {
    private final UtenteDAO utenteDAO;

    public AutenticazioneService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    public AutenticazioneService() {
        this.utenteDAO = new UtenteDAO(); // Default per uso normale
    }

    /**
     * Valida i campi del form di registrazione (Server-Side Validation).
     * @return true se i campi sono validi, false altrimenti.
     */
    public boolean validaInputRegistrazione(String email, String nome, String cognome,
                                            String hashedPassword, String ruolo) {
        // Verifica campi non nulli
        if (email == null || nome == null || cognome == null ||
                hashedPassword == null || ruolo == null) {
            return false;
        }

        // Verifica lunghezza e formato dell'email
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[0].length() > 25) {
            return false;
        }

        // Verifica formato email con una regex
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            return false;
        }

        // Verifica nome e cognome (2-25 caratteri, lettere e spazi/accenti)
        if (!Pattern.matches("^[A-Za-zÀ-ÿ\\s']{2,25}$", nome) ||
                !Pattern.matches("^[A-Za-zÀ-ÿ\\s']{2,25}$", cognome)) {
            return false;
        }

        // Verifica ruolo
        if (!ruolo.equals("MENTOR") && !ruolo.equals("MENTEE")) {
            return false;
        }

        return true;
    }

    /**
     * Verifica se esiste già un utente con la email specificata.
     * @return true se l'email è già presente, false altrimenti.
     */
    public boolean isEmailRegistrata(String email) throws SQLException {
        Utente utente = utenteDAO.doRetrieveByEmail(email);
        return (utente != null);
    }

    /**
     * Esegue la seconda crittografia della password.
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

    /**
     * Salva un nuovo utente nel database.
     */
    public void registraNuovoUtente(String email, String nome, String cognome,
                                    String doubleHashedPassword, String ruolo) throws SQLException {
        Utente utente = new Utente();
        utente.setEmail(email);
        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setPassword(doubleHashedPassword);
        utente.setRuolo(ruolo);

        utenteDAO.doSave(utente);
    }

    /**
     * Tenta il login di un utente in base a email e password in chiaro.
     * @return l'oggetto Utente se le credenziali sono valide, altrimenti null.
     */
    public Utente effettuaLogin(String email, String passwordChiara) throws Exception {
        Utente utente = utenteDAO.doRetrieveByEmail(email);
        if (utente == null) {
            return null;
        }

        // Confrontiamo l'hash della password passata dal form
        // con quella salvata nel DB
        String passwordHashedDaForm = hashPassword(passwordChiara);
        if (!utente.getPassword().equals(passwordHashedDaForm)) {
            return null;
        }

        return utente;
    }

    /**
     * Controlla se l'utente (email) esiste nel database.
     * Restituisce true se esiste, false altrimenti.
     */
    public boolean checkEmailExists(String email) {
        try {
            return utenteDAO.doRetrieveByEmail(email) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
