package model.beans;

import java.util.Objects;

public class Pagamento {
    private int idPagamento;
    private int idPrenotazione;
    private String metodoPagamento;
    private double totalePagato;
    private String statusPagamento;
    private String dataPagamento;

    public Pagamento() {
    }

    // Getters e Setters
    public int getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }

    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(int idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public double getTotalePagato() {
        return totalePagato;
    }

    public void setTotalePagato(double totalePagato) {
        this.totalePagato = totalePagato;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    @Override
    public String toString() {
        return "Pagamento{" +
                "idPagamento=" + idPagamento +
                ", idPrenotazione=" + idPrenotazione +
                ", metodoPagamento='" + metodoPagamento + '\'' +
                ", totalePagato=" + totalePagato +
                ", statusPagamento='" + statusPagamento + '\'' +
                ", dataPagamento='" + dataPagamento + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pagamento)) return false;
        Pagamento pagamento = (Pagamento) o;
        return getIdPagamento() == pagamento.getIdPagamento() && getIdPrenotazione() == pagamento.getIdPrenotazione() && Double.compare(getTotalePagato(), pagamento.getTotalePagato()) == 0 && Objects.equals(getMetodoPagamento(), pagamento.getMetodoPagamento()) && Objects.equals(getStatusPagamento(), pagamento.getStatusPagamento()) && Objects.equals(getDataPagamento(), pagamento.getDataPagamento());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPagamento(), getIdPrenotazione(), getMetodoPagamento(), getTotalePagato(), getStatusPagamento(), getDataPagamento());
    }
}
