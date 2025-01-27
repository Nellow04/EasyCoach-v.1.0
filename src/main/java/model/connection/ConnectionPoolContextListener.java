package model.connection;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ConnectionPoolContextListener implements ServletContextListener {
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.closePool();
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Il pool viene inizializzato automaticamente al primo utilizzo
    }
} 