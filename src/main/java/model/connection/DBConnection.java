package model.connection;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static DataSource dataSource;
    
    static {
        try {
            PoolProperties p = new PoolProperties();
            p.setUrl("jdbc:mysql://localhost:3306/easycoach");
            p.setDriverClassName("com.mysql.cj.jdbc.Driver");
            p.setUsername("root");
            p.setPassword("Admin123");
            
            // Configurazione base del pool
            p.setMaxActive(10);
            p.setInitialSize(5);
            p.setMinIdle(5);
            p.setMaxWait(10000);
            
            // Test delle connessioni
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setValidationInterval(30000);
            
            dataSource = new DataSource();
            dataSource.setPoolProperties(p);
            
            LOGGER.info("Connection pool inizializzato con successo");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'inizializzazione del connection pool", e);
            throw new RuntimeException("Impossibile inizializzare il connection pool", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Il connection pool non è stato inizializzato");
        }
        return dataSource.getConnection();
    }
    
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close(true);
            LOGGER.info("Connection pool chiuso");
        }
    }
    
    // Impedisce l'istanziazione della classe
    private DBConnection() {}
}
