package model.dto;

import java.time.LocalDateTime;

public class PrenotazioneDetailsDTO {
    private int idPrenotazione;
    private String titolo;
    private String mentorName;
    private String menteeName;
    private String orario;
    private String linkVideoconferenza;
    private LocalDateTime dataPrenotazione;
    private String statusPrenotazione;

    public PrenotazioneDetailsDTO() {
    }

    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(int idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMenteeName() {
        return menteeName;
    }

    public void setMenteeName(String menteeName) {
        this.menteeName = menteeName;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    public String getLinkVideoconferenza() {
        return linkVideoconferenza;
    }

    public void setLinkVideoconferenza(String linkVideoconferenza) {
        this.linkVideoconferenza = linkVideoconferenza;
    }

    public LocalDateTime getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(LocalDateTime dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
    }

    public String getStatusPrenotazione() {
        return statusPrenotazione;
    }

    public void setStatusPrenotazione(String statusPrenotazione) {
        this.statusPrenotazione = statusPrenotazione;
    }
}
