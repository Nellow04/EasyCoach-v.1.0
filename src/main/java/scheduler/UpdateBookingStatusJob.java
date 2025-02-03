package scheduler;

import model.dao.PrenotazioneDAO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateBookingStatusJob implements Job {
    private static final Logger logger = Logger.getLogger(UpdateBookingStatusJob.class.getName());
    private final PrenotazioneDAO prenotazioneDAO;

    // Costruttore di default
    public UpdateBookingStatusJob() {
        this.prenotazioneDAO = new PrenotazioneDAO();
    }

    // Costruttore per i test
    public UpdateBookingStatusJob(PrenotazioneDAO prenotazioneDAO) {
        this.prenotazioneDAO = prenotazioneDAO;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            logger.info("Esecuzione job pulizia prenotazioni scadute...");
            prenotazioneDAO.updateExpiredBookings();
            logger.info("Job completato con successo");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'aggiornamento delle prenotazioni scadute", e);
            throw new JobExecutionException("Errore durante l'aggiornamento delle prenotazioni scadute", e);
        }
    }
}