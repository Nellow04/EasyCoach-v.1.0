package sottosistemi.Prenotazione.service;

import model.beans.Pagamento;
import model.beans.Prenotazione;
import model.beans.Sessione;
import model.dao.PagamentoDAO;
import model.dao.PrenotazioneDAO;
import model.dao.SessioneDAO;
import websocket.NotificationWebSocket;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service che incapsula la logica di pagamento:
 * - Validazione dei dati della carta (se necessario)
 * - Creazione del record di pagamento
 * - Aggiornamento della prenotazione a "ATTIVA"
 * - Invio notifiche a MENTEE e MENTOR
 */
public class PaymentService {

    private final PagamentoDAO pagamentoDAO;
    private final PrenotazioneDAO prenotazioneDAO;
    private final SessioneDAO sessioneDAO;
    public PaymentService(PagamentoDAO pagamentoDAO, PrenotazioneDAO prenotazioneDAO, SessioneDAO sessioneDAO) {
        this.pagamentoDAO = pagamentoDAO;
        this.prenotazioneDAO = prenotazioneDAO;
        this.sessioneDAO = sessioneDAO;
    }

    public PaymentService() {
        this.pagamentoDAO = new PagamentoDAO();
        this.prenotazioneDAO = new PrenotazioneDAO();
        this.sessioneDAO = new SessioneDAO();
    }

    /**
     * Lancia eccezione se i dati carta non sono validi.
     * @throws ValidationException se la carta non rispetta i requisiti
     */
    public void validateCardPayment(String numeroCarta,
                                    String scadenzaGGMM,
                                    String scadenzaAnno,
                                    String cardHolder,
                                    String cvv) throws ValidationException {
        // Esempio di regex e controlli, invariati dalla Servlet
        if (numeroCarta == null || !numeroCarta.replace(" ", "").matches("^\\d{16}$")) {
            throw new ValidationException("Numero carta non valido");
        }

        if (scadenzaGGMM == null || scadenzaAnno == null ||
                !scadenzaGGMM.matches("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])") ||
                !scadenzaAnno.matches("\\d{4}")) {
            throw new ValidationException("Formato data scadenza non valido");
        }

        try {
            String[] parts = scadenzaGGMM.split("/");
            String dataCompleta = String.format("%s/%s/%s", parts[0], parts[1], scadenzaAnno);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate scadenza = LocalDate.parse(dataCompleta, formatter);
            if (scadenza.isBefore(LocalDate.now())) {
                throw new ValidationException("Carta scaduta");
            }
        } catch (Exception e) {
            throw new ValidationException("Data di scadenza non valida");
        }

        if (cardHolder == null || !cardHolder.matches("^[A-Za-zÀ-ÿ\\s']{2,}$")) {
            throw new ValidationException("Nome titolare carta non valido");
        }

        if (cvv == null || !cvv.matches("^[0-9]{3,4}$")) {
            throw new ValidationException("CVV non valido");
        }
    }

    /**
     * Esegue il processo di pagamento:
     * 1) Salva il pagamento
     * 2) Aggiorna la prenotazione a "ATTIVA" generando il link videoconferenza
     * 3) Invia notifiche a MENTEE e MENTOR
     */
    public Pagamento processPayment(int idPrenotazione, String metodoPagamentoParam, double totalePagato, String idUtenteSession) throws SQLException {
        // Crea il record Pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPrenotazione(idPrenotazione);

        // Mappa "GOOGLEPAY" -> "ALTRO" come nella Servlet
        if ("GOOGLEPAY".equals(metodoPagamentoParam)) {
            pagamento.setMetodoPagamento("ALTRO");
        } else {
            pagamento.setMetodoPagamento(metodoPagamentoParam);
        }
        pagamento.setTotalePagato(totalePagato);
        pagamento.setStatusPagamento("COMPLETATO");
        pagamento.setDataPagamento(LocalDateTime.now().toString());

        // Salvataggio
        pagamentoDAO.doSave(pagamento);

        // Aggiorna la prenotazione
        Prenotazione prenotazione = prenotazioneDAO.doFindById(idPrenotazione);
        if (prenotazione != null) {
            String meetingId = String.format("EasyCoach_%d_%d", idPrenotazione, System.currentTimeMillis());
            String jitsiLink = "https://meet.jit.si/" + meetingId;

            prenotazione.setStatusPrenotazione("ATTIVA");
            prenotazione.setLinkVideoconferenza(jitsiLink);
            prenotazioneDAO.doUpdate(prenotazione);

            // Invia notifiche
            sendNotifications(prenotazione, idUtenteSession);
        }

        return pagamento;
    }

    /**
     * Invia notifiche via WebSocket al mentee e al mentor.
     */
    private void sendNotifications(Prenotazione prenotazione, String idUtenteSession) {
        try {
            Sessione sessione = sessioneDAO.doFindById(prenotazione.getIdSessione());
            if (sessione == null || idUtenteSession == null) {
                System.out.println("ERRORE: sessione o idUtente non trovati per la notifica");
                return;
            }

            String menteeMessage = "Pagamento completato con successo per la sessione " + prenotazione.getIdPrenotazione();
            String mentorMessage = "Nuova prenotazione confermata per la sessione " + prenotazione.getIdPrenotazione();

            NotificationWebSocket.notifyMentee(idUtenteSession, menteeMessage);
            NotificationWebSocket.notifyMentor(String.valueOf(sessione.getIdUtente()), mentorMessage);

            System.out.println("Notifiche inviate correttamente a mentee e mentor");
        } catch (Exception e) {
            System.out.println("Errore nell'invio delle notifiche: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Eccezione di validazione interna.
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
