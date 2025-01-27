package model.dao;


import model.beans.Pagamento;
import model.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagamentoDAO {

    private static final String INSERT_PAGAMENTO =
            "INSERT INTO Pagamento (idPrenotazione, metodoPagamento, totalePagato, statusPagamento, dataPagamento) "
                    + "VALUES (?,?,?,?,?)";

    private static final String SELECT_PAGAMENTO_BY_ID =
            "SELECT * FROM Pagamento WHERE idPagamento = ?";



    // Metodo per salvare un pagamento
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

    // Metodo per ottenere un Pagamento dato il suo ID
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
}
