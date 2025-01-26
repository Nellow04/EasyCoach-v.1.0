package model.dao;

import model.beans.Utente;
import model.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    private static final String INSERT_UTENTE =
            "INSERT INTO Utente (email, nome, cognome, password, ruolo, isDeleted) VALUES (?,?,?,?,?,FALSE)";

    private static final String SELECT_UTENTE_BY_ID =
            "SELECT * FROM Utente WHERE idUtente = ? AND isDeleted = FALSE";

    private static final String UPDATE_UTENTE =
            "UPDATE Utente SET email=?, nome=?, cognome=?, password=?, ruolo=? WHERE idUtente=? AND isDeleted = FALSE";

    private static final String DELETE_UTENTE =
            "UPDATE Utente SET isDeleted = TRUE WHERE idUtente=?";

    private static final String SELECT_ALL_UTENTI =
            "SELECT * FROM Utente WHERE isDeleted = FALSE";

    private static final String SELECT_UTENTE_BY_EMAIL =
            "SELECT * FROM Utente WHERE email = ? AND isDeleted = FALSE";

    private static final String UPDATE_UTENTE_PASSWORD =
            "UPDATE Utente SET password = ? WHERE idUtente = ? AND isDeleted = FALSE";

    private static final String SELECT_TOP_MENTORS =
            "SELECT u.*, (SELECT COUNT(*) FROM Sessione s WHERE s.idMentor = u.idUtente) as sessioni_count " +
            "FROM Utente u " +
            "WHERE u.ruolo = 'mentor' AND u.isDeleted = FALSE " +
            "ORDER BY sessioni_count DESC " +
            "LIMIT 3";

    private static final String SELECT_ALL_MENTORS =
            "SELECT * FROM Utente WHERE ruolo = 'mentor' AND isDeleted = FALSE";

    // Salva un nuovo utente
    public void doSave(Utente utente) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_UTENTE)) {

            ps.setString(1, utente.getEmail());
            ps.setString(2, utente.getNome());
            ps.setString(3, utente.getCognome());
            ps.setString(4, utente.getPassword());
            ps.setString(5, utente.getRuolo());
            ps.executeUpdate();
        }
    }

    // Trova un utente per ID
    public Utente doFindById(int idUtente) throws SQLException {
        Utente utente = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_UTENTE_BY_ID)) {

            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente();
                    utente.setIdUtente(rs.getInt("idUtente"));
                    utente.setEmail(rs.getString("email"));
                    utente.setNome(rs.getString("nome"));
                    utente.setCognome(rs.getString("cognome"));
                    utente.setPassword(rs.getString("password"));
                    utente.setRuolo(rs.getString("ruolo"));
                }
            }
        }
        return utente;
    }

    // Aggiorna un utente esistente
    public void doUpdate(Utente utente) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_UTENTE)) {

            ps.setString(1, utente.getEmail());
            ps.setString(2, utente.getNome());
            ps.setString(3, utente.getCognome());
            ps.setString(4, utente.getPassword());
            ps.setString(5, utente.getRuolo());
            ps.setInt(6, utente.getIdUtente());

            ps.executeUpdate();
        }
    }

    // Aggiorna password dell'utente
    public void doUpdatePassword(Utente utente) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_UTENTE_PASSWORD)) {

            ps.setString(1, utente.getPassword());
            ps.setInt(2, utente.getIdUtente());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aggiornamento password fallito: utente non trovato.");
            }
        }
    }

    // Elimina un utente per ID
    public void doDelete(int idUtente) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_UTENTE)) {

            ps.setInt(1, idUtente);
            ps.executeUpdate();
        }
    }

    // Trova tutti gli utenti
    public List<Utente> doFindAll() throws SQLException {
        List<Utente> utenti = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_UTENTI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utente utente = new Utente();
                utente.setIdUtente(rs.getInt("idUtente"));
                utente.setEmail(rs.getString("email"));
                utente.setNome(rs.getString("nome"));
                utente.setCognome(rs.getString("cognome"));
                utente.setPassword(rs.getString("password"));
                utente.setRuolo(rs.getString("ruolo"));
                utenti.add(utente);
            }
        }
        return utenti;
    }

    // Trova un utente per email
    public Utente doRetrieveByEmail(String email) throws SQLException {
        Utente utente = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_UTENTE_BY_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente();
                    utente.setIdUtente(rs.getInt("idUtente"));
                    utente.setEmail(rs.getString("email"));
                    utente.setNome(rs.getString("nome"));
                    utente.setCognome(rs.getString("cognome"));
                    utente.setPassword(rs.getString("password"));
                    utente.setRuolo(rs.getString("ruolo"));
                }
            }
        }
        return utente;
    }

    // Trova tutti i mentor
    public List<Utente> getAllMentors() throws SQLException {
        List<Utente> mentors = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_MENTORS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utente mentor = new Utente();
                mentor.setIdUtente(rs.getInt("idUtente"));
                mentor.setEmail(rs.getString("email"));
                mentor.setNome(rs.getString("nome"));
                mentor.setCognome(rs.getString("cognome"));
                mentor.setRuolo(rs.getString("ruolo"));
                mentors.add(mentor);
            }
        }
        return mentors;
    }
}
