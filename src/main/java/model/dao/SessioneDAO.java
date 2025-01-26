package model.dao;

import model.beans.Sessione;
import model.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessioneDAO {

    private static final String INSERT_SESSIONE =
            "INSERT INTO Sessione (idUtente, titolo, descrizione, prezzo, immagine, statusSessione) "
                    + "VALUES (?,?,?,?,?,?)";

    private static final String SELECT_SESSIONE_BY_ID =
            "SELECT * FROM Sessione WHERE idSessione = ?";

    private static final String UPDATE_SESSIONE =
            "UPDATE Sessione SET idUtente=?, titolo=?, descrizione=?, prezzo=?, immagine=?, statusSessione=? "
                    + "WHERE idSessione=?";

    private static final String DELETE_SESSIONE =
            "DELETE FROM Sessione WHERE idSessione=?";

    private static final String SELECT_ALL_SESSIONI =
            "SELECT s.* FROM Sessione s " +
                    "JOIN Utente u ON s.idUtente = u.idUtente AND u.isDeleted = FALSE " + // Aggiunto qui
                    "WHERE s.statusSessione != 'ARCHIVIATA'";

    private static final String SELECT_SESSIONI_BY_UTENTE =
            "SELECT * FROM Sessione WHERE idUtente = ? AND statusSessione != 'ARCHIVIATA' ORDER BY idSessione DESC";

    // Salva una nuova sessione
    public int doSave(Sessione sessione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SESSIONE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, sessione.getIdUtente());
            ps.setString(2, sessione.getTitolo());
            ps.setString(3, sessione.getDescrizione());
            ps.setDouble(4, sessione.getPrezzo());
            ps.setString(5, sessione.getImmagine());
            ps.setString(6, sessione.getStatusSessione());

            ps.executeUpdate();

            // Ottieni l'ID generato
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creazione sessione fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    public List<Sessione> getTreSessioniCasuali() {
        List<Sessione> sessioni = new ArrayList<>();
        String query = "SELECT * FROM Sessione WHERE statusSessione != 'ARCHIVIATA' ORDER BY RAND() LIMIT 3";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sessione sessione = new Sessione();
                sessione.setIdSessione(rs.getInt("idSessione"));
                sessione.setIdUtente(rs.getInt("idUtente"));
                sessione.setTitolo(rs.getString("titolo"));
                sessione.setDescrizione(rs.getString("descrizione"));
                sessione.setPrezzo(rs.getDouble("prezzo"));
                sessione.setImmagine(rs.getString("immagine"));
                sessione.setStatusSessione(rs.getString("statusSessione"));
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessioni;
    }

    // Trova una sessione per ID
    public Sessione doFindById(int idSessione) throws SQLException {
        Sessione sessione = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_SESSIONE_BY_ID)) {

            ps.setInt(1, idSessione);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sessione = new Sessione();
                    sessione.setIdSessione(rs.getInt("idSessione"));
                    sessione.setIdUtente(rs.getInt("idUtente"));
                    sessione.setTitolo(rs.getString("titolo"));
                    sessione.setDescrizione(rs.getString("descrizione"));
                    sessione.setPrezzo(rs.getDouble("prezzo"));
                    sessione.setImmagine(rs.getString("immagine"));
                    sessione.setStatusSessione(rs.getString("statusSessione"));
                }
            }
        }
        return sessione;
    }

    // Aggiorna una sessione esistente
    public void doUpdate(Sessione sessione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_SESSIONE)) {

            ps.setInt(1, sessione.getIdUtente());
            ps.setString(2, sessione.getTitolo());
            ps.setString(3, sessione.getDescrizione());
            ps.setDouble(4, sessione.getPrezzo());
            ps.setString(5, sessione.getImmagine());
            ps.setString(6, sessione.getStatusSessione());
            ps.setInt(7, sessione.getIdSessione());

            ps.executeUpdate();
        }
    }

    // Elimina una sessione per ID
    public void doDelete(int idSessione) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_SESSIONE)) {

            ps.setInt(1, idSessione);
            ps.executeUpdate();
        }
    }

    // Trova tutte le sessioni
    public List<Sessione> doFindAll() throws SQLException {
        List<Sessione> sessioni = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_SESSIONI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sessione sessione = new Sessione();
                sessione.setIdSessione(rs.getInt("idSessione"));
                sessione.setIdUtente(rs.getInt("idUtente"));
                sessione.setTitolo(rs.getString("titolo"));
                sessione.setDescrizione(rs.getString("descrizione"));
                sessione.setPrezzo(rs.getDouble("prezzo"));
                sessione.setImmagine(rs.getString("immagine"));
                sessione.setStatusSessione(rs.getString("statusSessione"));
                sessioni.add(sessione);
            }
        }
        return sessioni;
    }

    // Trova tutte le sessioni di un utente
    public List<Sessione> findByUserId(int userId) throws SQLException {
        List<Sessione> sessioni = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_SESSIONI_BY_UTENTE)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sessione s = new Sessione();
                    s.setIdSessione(rs.getInt("idSessione"));
                    s.setIdUtente(rs.getInt("idUtente"));
                    s.setTitolo(rs.getString("titolo"));
                    s.setDescrizione(rs.getString("descrizione"));
                    s.setPrezzo(rs.getDouble("prezzo"));
                    s.setImmagine(rs.getString("immagine"));
                    s.setStatusSessione(rs.getString("statusSessione"));
                    sessioni.add(s);
                }
            }
        }
        return sessioni;
    }

    // Recupera le sessioni correlate
    public List<Sessione> doFindRelated(int idSessione, int limit) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM sessione WHERE idSessione != ? AND statusSessione = 'ATTIVA' ORDER BY RAND() LIMIT ?");
            ps.setInt(1, idSessione);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            List<Sessione> sessioni = new ArrayList<>();
            while (rs.next()) {
                Sessione s = extract(rs);
                sessioni.add(s);
            }
            return sessioni;
        }
    }

    public List<Sessione> findByTitleLike(String titlePart) throws SQLException {
        List<Sessione> results = new ArrayList<>();
        String sql = "SELECT s.* FROM Sessione s " +
                "JOIN Utente u ON s.idUtente = u.idUtente AND u.isDeleted = FALSE " + // Aggiunto qui
                "WHERE s.titolo LIKE ? AND s.statusSessione != 'ARCHIVIATA' " +
                "ORDER BY s.idSessione DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + titlePart + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sessione s = new Sessione();
                    s.setIdSessione(rs.getInt("idSessione"));
                    s.setIdUtente(rs.getInt("idUtente"));
                    s.setTitolo(rs.getString("titolo"));
                    s.setDescrizione(rs.getString("descrizione"));
                    s.setPrezzo(rs.getDouble("prezzo"));
                    s.setImmagine(rs.getString("immagine"));
                    s.setStatusSessione(rs.getString("statusSessione"));
                    results.add(s);
                }
            }
        }
        return results;
    }

    // Archivia una sessione
    public void archiveSession(int idSessione, boolean isAdmin) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            // Se non è admin, verifica se ci sono prenotazioni associate
            if (!isAdmin) {
                String checkBookings = "SELECT COUNT(*) FROM Prenotazione WHERE idSessione = ?";
                try (PreparedStatement checkPs = con.prepareStatement(checkBookings)) {
                    checkPs.setInt(1, idSessione);
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Non puoi archiviare questa sessione perché ha delle prenotazioni associate");
                    }
                }
            }

            // Recupera la sessione
            Sessione sessione = doFindById(idSessione);
            if (sessione != null) {
                sessione.setStatusSessione("ARCHIVIATA");
                doUpdate(sessione);
            } else {
                throw new SQLException("Sessione non trovata");
            }
        }
    }

    private Sessione extract(ResultSet rs) throws SQLException {
        Sessione sessione = new Sessione();
        sessione.setIdSessione(rs.getInt("idSessione"));
        sessione.setIdUtente(rs.getInt("idUtente"));
        sessione.setTitolo(rs.getString("titolo"));
        sessione.setDescrizione(rs.getString("descrizione"));
        sessione.setPrezzo(rs.getDouble("prezzo"));
        sessione.setImmagine(rs.getString("immagine"));
        sessione.setStatusSessione(rs.getString("statusSessione"));
        return sessione;
    }
}