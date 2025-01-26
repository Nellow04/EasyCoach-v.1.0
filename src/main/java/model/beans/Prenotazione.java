package model.beans;

import java.time.LocalDateTime;
import java.util.Objects;

public class Prenotazione {
    private int idPrenotazione;
    private int idUtente;
    private int idTimeslot;
    private int idSessione;
    private LocalDateTime dataPrenotazione;  // Data e ora specifiche della prenotazione
    private String linkVideoconferenza;
    private String statusPrenotazione;

    public Prenotazione() {
    }

    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(int idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public int getIdTimeslot() {
        return idTimeslot;
    }

    public void setIdTimeslot(int idTimeslot) {
        this.idTimeslot = idTimeslot;
    }

    public int getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(int idSessione) {
        this.idSessione = idSessione;
    }

    public LocalDateTime getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(LocalDateTime dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
    }

    public String getLinkVideoconferenza() {
        return linkVideoconferenza;
    }

    public void setLinkVideoconferenza(String linkVideoconferenza) {
        this.linkVideoconferenza = linkVideoconferenza;
    }

    public String getStatusPrenotazione() {
        return statusPrenotazione;
    }

    public void setStatusPrenotazione(String statusPrenotazione) {
        this.statusPrenotazione = statusPrenotazione;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "idPrenotazione=" + idPrenotazione +
                ", idUtente=" + idUtente +
                ", idTimeslot=" + idTimeslot +
                ", idSessione=" + idSessione +
                ", dataPrenotazione=" + dataPrenotazione +
                ", linkVideoconferenza='" + linkVideoconferenza + '\'' +
                ", statusPrenotazione='" + statusPrenotazione + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prenotazione)) return false;
        Prenotazione that = (Prenotazione) o;
        return getIdPrenotazione() == that.getIdPrenotazione() &&
                getIdUtente() == that.getIdUtente() &&
                getIdTimeslot() == that.getIdTimeslot() &&
                getIdSessione() == that.getIdSessione() &&
                Objects.equals(getDataPrenotazione(), that.getDataPrenotazione()) &&
                Objects.equals(getLinkVideoconferenza(), that.getLinkVideoconferenza()) &&
                Objects.equals(getStatusPrenotazione(), that.getStatusPrenotazione());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPrenotazione(), getIdUtente(), getIdTimeslot(),
                getIdSessione(), getDataPrenotazione(), getLinkVideoconferenza(), getStatusPrenotazione());
    }
}