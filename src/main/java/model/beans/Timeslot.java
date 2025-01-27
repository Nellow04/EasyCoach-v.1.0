package model.beans;

import java.util.Objects;

public class Timeslot {
    private int idTimeslot;
    private int idSessione;
    private int giorno;
    private int orario;

    public Timeslot() {
    }

    // Getters e Setters
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

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public int getOrario() {
        return orario;
    }

    public void setOrario(int orario) {
        this.orario = orario;
    }

    @Override
    public String toString() {
        return "Timeslot{" +
                "idTimeslot=" + idTimeslot +
                ", idSessione=" + idSessione +
                ", giorno=" + giorno +
                ", orario=" + orario +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeslot timeslot = (Timeslot) o;
        return idTimeslot == timeslot.idTimeslot &&
                idSessione == timeslot.idSessione &&
                giorno == timeslot.giorno &&
                orario == timeslot.orario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTimeslot, idSessione, giorno, orario);
    }
}