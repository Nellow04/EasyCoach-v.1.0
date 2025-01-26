package model.dao;

import model.beans.Timeslot;
import model.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimeslotDAO {

    private static final String INSERT_TIMESLOT =
            "INSERT INTO Timeslot (idSessione, giorno, orario) VALUES (?,?,?)";

    private static final String SELECT_TIMESLOT_BY_ID =
            "SELECT * FROM Timeslot WHERE idTimeslot = ?";

    private static final String UPDATE_TIMESLOT =
            "UPDATE Timeslot SET idSessione=?, giorno=?, orario=? WHERE idTimeslot=?";

    private static final String DELETE_TIMESLOT =
            "DELETE FROM Timeslot WHERE idTimeslot=?";

    private static final String SELECT_ALL_TIMESLOT =
            "SELECT * FROM Timeslot";

    private static final String SELECT_TIMESLOT_BY_MENTOR =
            "SELECT t.* " +
                    "FROM Timeslot t " +
                    "JOIN Sessione s ON t.idSessione = s.idSessione " +
                    "WHERE s.idUtente = ? AND s.statusSessione != 'ARCHIVIATA'";

    private static final String SELECT_TIMESLOT_BY_SESSION =
            "SELECT t.* " +
                    "FROM Timeslot t " +
                    "JOIN Sessione s ON t.idSessione = s.idSessione " +
                    "WHERE t.idSessione = ? AND s.statusSessione != 'ARCHIVIATA' " +
                    "ORDER BY t.giorno, t.orario";

    // Salva un nuovo timeslot
    public void doSave(Timeslot timeslot) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_TIMESLOT)) {

            ps.setInt(1, timeslot.getIdSessione());
            ps.setInt(2, timeslot.getGiorno());
            ps.setInt(3, timeslot.getOrario());
            ps.executeUpdate();
        }
    }

    // Trova un timeslot per ID
    public Timeslot doFindById(int idTimeslot) throws SQLException {
        Timeslot timeslot = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_TIMESLOT_BY_ID)) {

            ps.setInt(1, idTimeslot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    timeslot = new Timeslot();
                    timeslot.setIdTimeslot(rs.getInt("idTimeslot"));
                    timeslot.setIdSessione(rs.getInt("idSessione"));
                    timeslot.setGiorno(rs.getInt("giorno"));
                    timeslot.setOrario(rs.getInt("orario"));
                }
            }
        }
        return timeslot;
    }

    // Aggiorna un timeslot esistente
    public void doUpdate(Timeslot timeslot) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_TIMESLOT)) {

            ps.setInt(1, timeslot.getIdSessione());
            ps.setInt(2, timeslot.getGiorno());
            ps.setInt(3, timeslot.getOrario());
            ps.setInt(4, timeslot.getIdTimeslot());

            ps.executeUpdate();
        }
    }

    // Elimina un timeslot per ID
    public void doDelete(int idTimeslot) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_TIMESLOT)) {

            ps.setInt(1, idTimeslot);
            ps.executeUpdate();
        }
    }

    // Trova tutti i timeslot
    public List<Timeslot> doFindAll() throws SQLException {
        List<Timeslot> timeslots = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_TIMESLOT);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timeslot t = new Timeslot();
                t.setIdTimeslot(rs.getInt("idTimeslot"));
                t.setIdSessione(rs.getInt("idSessione"));
                t.setGiorno(rs.getInt("giorno"));
                t.setOrario(rs.getInt("orario"));
                timeslots.add(t);
            }
        }
        return timeslots;
    }

    // Trova tutti i timeslot occupati di un mentor
    public List<Timeslot> findByMentorId(int mentorId) throws SQLException {
        List<Timeslot> timeslots = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_TIMESLOT_BY_MENTOR)) {

            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timeslot t = new Timeslot();
                    t.setIdTimeslot(rs.getInt("idTimeslot"));
                    t.setIdSessione(rs.getInt("idSessione"));
                    t.setGiorno(rs.getInt("giorno"));
                    t.setOrario(rs.getInt("orario"));
                    timeslots.add(t);
                }
            }
        }
        return timeslots;
    }

    // Trova tutti i timeslot di una sessione
    public List<Timeslot> findBySessionId(int sessionId) throws SQLException {
        List<Timeslot> timeslots = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_TIMESLOT_BY_SESSION)) {

            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timeslot t = new Timeslot();
                    t.setIdTimeslot(rs.getInt("idTimeslot"));
                    t.setIdSessione(rs.getInt("idSessione"));
                    t.setGiorno(rs.getInt("giorno"));
                    t.setOrario(rs.getInt("orario"));
                    timeslots.add(t);
                }
            }
        }
        return timeslots;
    }

    // Elimina tutti i timeslot di una sessione
    public void doDeleteBySessione(int idSessione) throws SQLException {
        String DELETE_TIMESLOTS_BY_SESSION = "DELETE FROM Timeslot WHERE idSessione = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_TIMESLOTS_BY_SESSION)) {

            ps.setInt(1, idSessione);
            ps.executeUpdate();
        }
    }
}