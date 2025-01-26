package model.dao;


import model.beans.Pagamento;
import model.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    private static final String INSERT_PAGAMENTO =
            "INSERT INTO Pagamento (idPrenotazione, metodoPagamento, totalePagato, statusPagamento, dataPagamento) "
                    + "VALUES (?,?,?,?,?)";

    private static final String SELECT_PAGAMENTO_BY_ID =
            "SELECT * FROM Pagamento WHERE idPagamento = ?";

    private static final String UPDATE_PAGAMENTO =
            "UPDATE Pagamento SET idPrenotazione=?, metodoPagamento=?, totalePagato=?, statusPagamento=?, dataPagamento=? "
                    + "WHERE idPagamento=?";

    private static final String DELETE_PAGAMENTO =
            "DELETE FROM Pagamento WHERE idPagamento=?";

    private static final String SELECT_ALL_PAGAMENTI =
            "SELECT * FROM Pagamento";

    // Salva un nuovo pagamento
    public void doSave(Pagamento pagamento) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_PAGAMENTO)) {

            ps.setInt(1, pagamento.getIdPrenotazione());
            ps.setString(2, pagamento.getMetodoPagamento());
            ps.setDouble(3, pagamento.getTotalePagato());
            ps.setString(4, pagamento.getStatusPagamento());
            ps.setString(5, pagamento.getDataPagamento());
            ps.executeUpdate();
        }
    }

    // Trova un pagamento per ID
    public Pagamento doFindById(int idPagamento) throws SQLException {
        Pagamento pagamento = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_PAGAMENTO_BY_ID)) {

            ps.setInt(1, idPagamento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagamento = new Pagamento();
                    pagamento.setIdPagamento(rs.getInt("idPagamento"));
                    pagamento.setIdPrenotazione(rs.getInt("idPrenotazione"));
                    pagamento.setMetodoPagamento(rs.getString("metodoPagamento"));
                    pagamento.setTotalePagato(rs.getDouble("totalePagato"));
                    pagamento.setStatusPagamento(rs.getString("statusPagamento"));
                    pagamento.setDataPagamento(rs.getString("dataPagamento"));
                }
            }
        }
        return pagamento;
    }

    // Aggiorna un pagamento esistente
    public void doUpdate(Pagamento pagamento) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_PAGAMENTO)) {

            ps.setInt(1, pagamento.getIdPrenotazione());
            ps.setString(2, pagamento.getMetodoPagamento());
            ps.setDouble(3, pagamento.getTotalePagato());
            ps.setString(4, pagamento.getStatusPagamento());
            ps.setString(5, pagamento.getDataPagamento());
            ps.setInt(6, pagamento.getIdPagamento());

            ps.executeUpdate();
        }
    }

    // Elimina un pagamento per ID
    public void doDelete(int idPagamento) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_PAGAMENTO)) {

            ps.setInt(1, idPagamento);
            ps.executeUpdate();
        }
    }

    // Trova tutti i pagamenti
    public List<Pagamento> doFindAll() throws SQLException {
        List<Pagamento> pagamenti = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_PAGAMENTI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pagamento p = new Pagamento();
                p.setIdPagamento(rs.getInt("idPagamento"));
                p.setIdPrenotazione(rs.getInt("idPrenotazione"));
                p.setMetodoPagamento(rs.getString("metodoPagamento"));
                p.setTotalePagato(rs.getDouble("totalePagato"));
                p.setStatusPagamento(rs.getString("statusPagamento"));
                p.setDataPagamento(rs.getString("dataPagamento"));
                pagamenti.add(p);
            }
        }
        return pagamenti;
    }
}
