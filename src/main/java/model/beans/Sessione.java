package model.beans;

import java.util.Objects;

public class Sessione {
    private int idSessione;
    private int idUtente;
    private String titolo;
    private String descrizione;
    private double prezzo;
    private String immagine;
    private String statusSessione;

    public Sessione() {
    }

    // Getters e Setters
    public int getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(int idSessione) {
        this.idSessione = idSessione;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public String getStatusSessione() {
        return statusSessione;
    }

    public void setStatusSessione(String statusSessione) {
        this.statusSessione = statusSessione;
    }

    @Override
    public String toString() {
        return "Sessione{" +
                "idSessione=" + idSessione +
                ", idUtente=" + idUtente +
                ", titolo='" + titolo + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                ", immagine='" + immagine + '\'' +
                ", statusSessione='" + statusSessione + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sessione)) return false;
        Sessione sessione = (Sessione) o;
        return getIdSessione() == sessione.getIdSessione() && getIdUtente() == sessione.getIdUtente() && Double.compare(getPrezzo(), sessione.getPrezzo()) == 0 && Objects.equals(getTitolo(), sessione.getTitolo()) && Objects.equals(getDescrizione(), sessione.getDescrizione()) && Objects.equals(getImmagine(), sessione.getImmagine()) && Objects.equals(getStatusSessione(), sessione.getStatusSessione());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdSessione(), getIdUtente(), getTitolo(), getDescrizione(), getPrezzo(), getImmagine(), getStatusSessione());
    }
}