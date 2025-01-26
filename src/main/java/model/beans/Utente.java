package model.beans;

import java.util.Objects;

public class Utente {
    private int idUtente;
    private String email;
    private String nome;
    private String cognome;
    private String password;
    private String ruolo;

    public Utente() {
    }

    // Getters e Setters
    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "idUtente=" + idUtente +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", password='" + password + '\'' +
                ", ruolo='" + ruolo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return getIdUtente() == utente.getIdUtente() && Objects.equals(getEmail(), utente.getEmail()) && Objects.equals(getNome(), utente.getNome()) && Objects.equals(getCognome(), utente.getCognome()) && Objects.equals(getPassword(), utente.getPassword()) && Objects.equals(getRuolo(), utente.getRuolo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUtente(), getEmail(), getNome(), getCognome(), getPassword(), getRuolo());
    }
}
