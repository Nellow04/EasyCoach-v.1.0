package model.dao;

import model.beans.Prenotazione;
import model.dto.PrenotazioneDetailsDTO;
import model.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PrenotazioneDAO {

    private static final String INSERT_PRENOTAZIONE =
            "INSERT INTO Prenotazione (idUtente, idTimeslot, idSessione, dataPrenotazione, statusPrenotazione) "
                    + "VALUES (?,?,?,?,?)";

    private static final String SELECT_PRENOTAZIONE_BY_ID =
            "SELECT * FROM Prenotazione WHERE idPrenotazione = ?";

    private static final String UPDATE_PRENOTAZIONE =
            "UPDATE Prenotazione SET idUtente=?, idTimeslot=?, idSessione=?, dataPrenotazione=?, statusPrenotazione=?, linkVideoconferenza=? "
                    + "WHERE idPrenotazione=?";

    private static final String DELETE_PRENOTAZIONE =
            "DELETE FROM Prenotazione WHERE idPrenotazione=?";

    private static final String SELECT_ALL_PRENOTAZIONI =
            "SELECT * FROM Prenotazione";

    private static final String CHECK_DISPONIBILITA =
            "SELECT COUNT(*) FROM Prenotazione WHERE idTimeslot = ? AND dataPrenotazione = ? AND statusPrenotazione != 'ANNULLATA'";

    private static final String CHECK_TIMESLOT_STATUS =
            "SELECT statusPrenotazione FROM Prenotazione " +
            "WHERE idTimeslot = ? AND dataPrenotazione = ?";

    private static final String UPDATE_EXPIRED_BOOKINGS = 
            "UPDATE Prenotazione SET statusPrenotazione = 'ANNULLATA' " +
            "WHERE statusPrenotazione = 'IN_ATTESA' " +
            "AND TIMESTAMPDIFF(MINUTE, timestampCreazione, NOW()) > 15 " +
            "AND idPrenotazione NOT IN (SELECT idPrenotazione FROM Pagamento WHERE statusPagamento = 'IN_CORSO')";

    private static final String UPDATE_COMPLETED_BOOKINGS =
            "UPDATE Prenotazione p " +
            "JOIN Timeslot t ON p.idTimeslot = t.idTimeslot " +
            "SET p.statusPrenotazione = 'CONCLUSA' " +
            "WHERE p.statusPrenotazione = 'ATTIVA' " +
            "AND (p.dataPrenotazione < ? " +
            "     OR (p.dataPrenotazione = ? AND (t.orario + 1) <= ?))";

    private static final String SELECT_ACTIVE_PRENOTAZIONI_BY_MENTEE =
            "SELECT p.* " +
            "FROM Prenotazione p " +
            "WHERE p.idUtente = ? AND p.statusPrenotazione = 'ATTIVA' " +
            "ORDER BY p.dataPrenotazione ASC";

    private static final String SELECT_COMPLETED_PRENOTAZIONI_BY_MENTEE =
            "SELECT p.* " +
            "FROM Prenotazione p " +
            "WHERE p.idUtente = ? AND p.statusPrenotazione = 'CONCLUSA' " +
            "ORDER BY p.dataPrenotazione DESC";

    private static final String SELECT_ACTIVE_PRENOTAZIONI_DETAILS_BY_MENTEE =
            "SELECT p.*, s.titolo, u.nome, u.cognome, t.orario " +
                    "FROM Prenotazione p " +
                    "JOIN Sessione s ON p.idSessione = s.idSessione " +
                    "JOIN Utente u ON s.idUtente = u.idUtente AND u.isDeleted = FALSE " +
                    "JOIN Timeslot t ON p.idTimeslot = t.idTimeslot " +
                    "WHERE p.idUtente = ? " +
                    "AND p.statusPrenotazione = 'ATTIVA' " +
                    "AND s.statusSessione != 'ARCHIVIATA' " +  // Aggiunto controllo per sessioni non archiviate
                    "ORDER BY p.dataPrenotazione ASC, t.orario ASC";

    private static final String SELECT_COMPLETED_PRENOTAZIONI_DETAILS_BY_MENTEE =
            "SELECT p.*, s.titolo, u.nome, u.cognome, t.orario " +
            "FROM Prenotazione p " +
            "JOIN Sessione s ON p.idSessione = s.idSessione " +
            "JOIN Utente u ON s.idUtente = u.idUtente " +
            "JOIN Timeslot t ON p.idTimeslot = t.idTimeslot " +
            "WHERE p.idUtente = ? AND p.statusPrenotazione = 'CONCLUSA' " +
            "ORDER BY p.dataPrenotazione DESC, t.orario DESC";

    private static final String SELECT_ACTIVE_PRENOTAZIONI_DETAILS_BY_MENTOR =
            "SELECT p.*, s.titolo, " +
                    "mentor.nome AS mentor_nome, mentor.cognome AS mentor_cognome, " +  // Dati del mentor
                    "mentee.nome AS mentee_nome, mentee.cognome AS mentee_cognome, " +  // Dati del mentee
                    "t.orario " +
                    "FROM Prenotazione p " +
                    "JOIN Sessione s ON p.idSessione = s.idSessione " +
                    "JOIN Utente mentor ON s.idUtente = mentor.idUtente AND mentor.isDeleted = FALSE " +  // Controllo isDeleted per il mentor
                    "JOIN Utente mentee ON p.idUtente = mentee.idUtente AND mentee.isDeleted = FALSE " +  // Controllo isDeleted per il mentee
                    "JOIN Timeslot t ON p.idTimeslot = t.idTimeslot " +
                    "WHERE s.idUtente = ? " +  // Filtro per il mentor
                    "AND p.statusPrenotazione = 'ATTIVA' " +  // Filtro per prenotazioni attive
                    "AND s.statusSessione != 'ARCHIVIATA' " +  // Filtro per sessioni non archiviate
                    "ORDER BY p.dataPrenotazione ASC, t.orario ASC";

    private static final String CHECK_ACTIVE_BOOKINGS_FOR_SESSION =
            "SELECT COUNT(*) FROM Prenotazione " +
            "WHERE idSessione = ? AND statusPrenotazione = 'ATTIVA'";

    private static final String CHECK_ACTIVE_BOOKINGS_FOR_USER =
            "SELECT COUNT(*) FROM Prenotazione p " +
            "LEFT JOIN Sessione s ON p.idSessione = s.idSessione " +
            "WHERE (p.idUtente = ? OR s.idUtente = ?) " + 
            "AND p.statusPrenotazione = 'ATTIVA'";

    // Verifica disponibilità per una data specificaa
    public boolean isDisponibile(int idTimeslot, LocalDateTime dataOra) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(CHECK_DISPONIBILITA)) {

            ps.setInt(1, idTimeslot);
            ps.setTimestamp(2, Timestamp.valueOf(dataOra));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    // Salva una nuova prenotazione
    public void doSave(Prenotazione prenotazione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_PRENOTAZIONE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, prenotazione.getIdUtente());
            ps.setInt(2, prenotazione.getIdTimeslot());
            ps.setInt(3, prenotazione.getIdSessione());
            ps.setTimestamp(4, Timestamp.valueOf(prenotazione.getDataPrenotazione()));
            ps.setString(5, prenotazione.getStatusPrenotazione());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                prenotazione.setIdPrenotazione(rs.getInt(1));
            }
        }
    }

    // Trova una prenotazione per ID
    public Prenotazione doFindById(int idPrenotazione) throws SQLException {
        Prenotazione prenotazione = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_PRENOTAZIONE_BY_ID)) {

            ps.setInt(1, idPrenotazione);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prenotazione = new Prenotazione();
                    prenotazione.setIdPrenotazione(rs.getInt("idPrenotazione"));
                    prenotazione.setIdUtente(rs.getInt("idUtente"));
                    prenotazione.setIdTimeslot(rs.getInt("idTimeslot"));
                    prenotazione.setIdSessione(rs.getInt("idSessione"));
                    prenotazione.setDataPrenotazione(rs.getTimestamp("dataPrenotazione").toLocalDateTime());
                    prenotazione.setStatusPrenotazione(rs.getString("statusPrenotazione"));
                    prenotazione.setLinkVideoconferenza(rs.getString("linkVideoconferenza"));
                }
            }
        }
        return prenotazione;
    }

    // Aggiorna una prenotazione esistente
    public void doUpdate(Prenotazione prenotazione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_PRENOTAZIONE)) {

            ps.setInt(1, prenotazione.getIdUtente());
            ps.setInt(2, prenotazione.getIdTimeslot());
            ps.setInt(3, prenotazione.getIdSessione());
            ps.setTimestamp(4, Timestamp.valueOf(prenotazione.getDataPrenotazione()));
            ps.setString(5, prenotazione.getStatusPrenotazione());
            ps.setString(6, prenotazione.getLinkVideoconferenza());
            ps.setInt(7, prenotazione.getIdPrenotazione());

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("UPDATE error.");
            }
        }
    }

    // Elimina una prenotazione per ID
    public void doDelete(int idPrenotazione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_PRENOTAZIONE)) {

            ps.setInt(1, idPrenotazione);
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("DELETE error.");
            }
        }
    }

    // Trova tutte le prenotazioni
    public List<Prenotazione> doFindAll() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_PRENOTAZIONI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setIdPrenotazione(rs.getInt("idPrenotazione"));
                p.setIdUtente(rs.getInt("idUtente"));
                p.setIdTimeslot(rs.getInt("idTimeslot"));
                p.setIdSessione(rs.getInt("idSessione"));
                p.setDataPrenotazione(rs.getTimestamp("dataPrenotazione").toLocalDateTime());
                p.setStatusPrenotazione(rs.getString("statusPrenotazione"));
                prenotazioni.add(p);
            }
        }
        return prenotazioni;
    }

    public List<Prenotazione> doRetrieveByUtente(int idUtente) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id_prenotazione, id_utente, id_timeslot, id_sessione, data_prenotazione, status_prenotazione FROM Prenotazione WHERE id_utente=?")) {

            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Prenotazione> prenotazioni = new ArrayList<>();
                while (rs.next()) {
                    Prenotazione p = new Prenotazione();
                    p.setIdPrenotazione(rs.getInt(1));
                    p.setIdUtente(rs.getInt(2));
                    p.setIdTimeslot(rs.getInt(3));
                    p.setIdSessione(rs.getInt(4));
                    p.setDataPrenotazione(rs.getTimestamp(5).toLocalDateTime());
                    p.setStatusPrenotazione(rs.getString(6));
                    prenotazioni.add(p);
                }
                return prenotazioni;
            }
        }
    }

    public void updateStatus(int idPrenotazione, String newStatus) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Prenotazione SET status_prenotazione=? WHERE id_prenotazione=?")) {

            ps.setString(1, newStatus);
            ps.setInt(2, idPrenotazione);
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("UPDATE error.");
            }
        }
    }

    public Map<String, Object> checkTimeslotStatus(int idTimeslot, LocalDateTime dataOra) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT statusPrenotazione FROM Prenotazione " +
                "WHERE idTimeslot = ? AND dataPrenotazione = ? " +
                "ORDER BY FIELD(statusPrenotazione, 'ATTIVA', 'CONCLUSA', 'IN_ATTESA', 'ANNULLATA') " +
                "LIMIT 1")) {

            ps.setInt(1, idTimeslot);
            ps.setTimestamp(2, Timestamp.valueOf(dataOra));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("statusPrenotazione");
                    // Se lo stato è ANNULLATA, il timeslot è disponibile
                    result.put("disponibile", status.equals("ANNULLATA"));
                    result.put("status", status);
                } else {
                    result.put("disponibile", true);
                }
            }
        }
        return result;
    }

    public void updateExpiredBookings() throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            // Prima aggiorna le prenotazioni scadute (non pagate in tempo)
            try (PreparedStatement ps = con.prepareStatement(UPDATE_EXPIRED_BOOKINGS)) {
                int updatedExpired = ps.executeUpdate();
                Logger.getLogger(PrenotazioneDAO.class.getName())
                      .info("Aggiornate " + updatedExpired + " prenotazioni scadute");
            }

            // Poi aggiorna le prenotazioni concluse (lezione terminata)
            LocalDateTime now = LocalDateTime.now();
            try (PreparedStatement ps = con.prepareStatement(UPDATE_COMPLETED_BOOKINGS)) {
                ps.setDate(1, Date.valueOf(now.toLocalDate()));
                ps.setDate(2, Date.valueOf(now.toLocalDate()));
                ps.setInt(3, now.getHour());
                
                int updatedCompleted = ps.executeUpdate();
                Logger.getLogger(PrenotazioneDAO.class.getName())
                      .info("Aggiornate " + updatedCompleted + " prenotazioni concluse");
            }
        }
    }





    // Trova i dettagli delle prenotazioni attive di un mentee
    public List<PrenotazioneDetailsDTO> findActiveDetailsByMenteeId(int idMentee) throws SQLException {
        List<PrenotazioneDetailsDTO> prenotazioni = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ACTIVE_PRENOTAZIONI_DETAILS_BY_MENTEE)) {

            ps.setInt(1, idMentee);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrenotazioneDetailsDTO p = new PrenotazioneDetailsDTO();
                    p.setIdPrenotazione(rs.getInt("idPrenotazione"));
                    p.setTitolo(rs.getString("titolo"));
                    p.setMentorName(rs.getString("nome") + " " + rs.getString("cognome"));
                    p.setOrario(rs.getInt("orario") + ":00");
                    p.setLinkVideoconferenza(rs.getString("linkVideoconferenza"));
                    p.setDataPrenotazione(rs.getTimestamp("dataPrenotazione").toLocalDateTime());
                    p.setStatusPrenotazione(rs.getString("statusPrenotazione"));
                    
                    prenotazioni.add(p);
                }
            }
        }
        return prenotazioni;
    }

    // Trova i dettagli delle prenotazioni concluse di un mentee
    public List<PrenotazioneDetailsDTO> findCompletedDetailsByMenteeId(int idMentee) throws SQLException {
        List<PrenotazioneDetailsDTO> prenotazioni = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_COMPLETED_PRENOTAZIONI_DETAILS_BY_MENTEE)) {

            ps.setInt(1, idMentee);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrenotazioneDetailsDTO p = new PrenotazioneDetailsDTO();
                    p.setIdPrenotazione(rs.getInt("idPrenotazione"));
                    p.setTitolo(rs.getString("titolo"));
                    p.setMentorName(rs.getString("nome") + " " + rs.getString("cognome"));
                    p.setOrario(rs.getInt("orario") + ":00");
                    p.setLinkVideoconferenza(rs.getString("linkVideoconferenza"));
                    p.setDataPrenotazione(rs.getTimestamp("dataPrenotazione").toLocalDateTime());
                    p.setStatusPrenotazione(rs.getString("statusPrenotazione"));
                    
                    prenotazioni.add(p);
                }
            }
        }
        return prenotazioni;
    }

    public List<PrenotazioneDetailsDTO> findActiveDetailsByMentorId(Integer idMentor) throws SQLException {
        List<PrenotazioneDetailsDTO> prenotazioni = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ACTIVE_PRENOTAZIONI_DETAILS_BY_MENTOR)) {

            ps.setInt(1, idMentor);  // Imposta l'ID del mentor
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrenotazioneDetailsDTO p = new PrenotazioneDetailsDTO();
                    p.setIdPrenotazione(rs.getInt("idPrenotazione"));
                    p.setTitolo(rs.getString("titolo"));

                    // Recupera il nome del mentor
                    p.setMentorName(rs.getString("mentor_nome") + " " + rs.getString("mentor_cognome"));

                    // Recupera il nome del mentee
                    p.setMenteeName(rs.getString("mentee_nome") + " " + rs.getString("mentee_cognome"));

                    p.setOrario(rs.getInt("orario") + ":00");
                    p.setLinkVideoconferenza(rs.getString("linkVideoconferenza"));
                    p.setDataPrenotazione(rs.getTimestamp("dataPrenotazione").toLocalDateTime());
                    p.setStatusPrenotazione(rs.getString("statusPrenotazione"));

                    prenotazioni.add(p);
                }
            }
        }
        return prenotazioni;
    }

    public boolean hasActiveBookings(int idSessione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(CHECK_ACTIVE_BOOKINGS_FOR_SESSION)) {

            ps.setInt(1, idSessione);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean hasActiveBookingsForUser(int idUtente) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(CHECK_ACTIVE_BOOKINGS_FOR_USER)) {
            
            ps.setInt(1, idUtente); // Per prenotazioni come mentee
            ps.setInt(2, idUtente); // Per prenotazioni come mentor
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}