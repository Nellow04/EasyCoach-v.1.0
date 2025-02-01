// Funzione per inizializzare le notifiche
function initializeNotifications() {
    const header = document.querySelector('.header');
    if (!header) {
        console.error('Header element not found');
        return;
    }

    const userType = header.dataset.userType;
    const userId = header.dataset.userId;
    
    console.log('User Type:', userType);
    console.log('User ID:', userId);
    
    if (!userType || !userId) {
        console.log('Missing user type or ID');
        return;
    }

    // Trova il badge delle notifiche e il link profilo
    const notificationBadge = document.querySelector('.notification-badge');
    const profileLink = document.querySelector('.profile-link');
    
    // Recupera il contatore dal localStorage
    let notificationCount = parseInt(localStorage.getItem('notificationCount') || '0');
    
    // Se ci sono notifiche non lette, mostra subito il badge
    if (notificationCount > 0 && notificationBadge) {
        notificationBadge.textContent = notificationCount;
        notificationBadge.style.display = 'flex';
    }

    // Aggiungi event listener per il click sul profilo
    if (profileLink) {
        profileLink.addEventListener('click', function(e) {
            // Non resettare se non ci sono notifiche
            if (notificationCount > 0) {
                notificationCount = 0;
                notificationBadge.style.display = 'none';
                notificationBadge.textContent = '0';
                localStorage.setItem('notificationCount', '0');
                localStorage.setItem('lastNotificationReset', new Date().getTime());
            }
        });
    }

    // Gestisci il logout
    const logoutLink = document.querySelector('a[href*="LogoutServlet"]');
    if (logoutLink) {
        logoutLink.addEventListener('click', function() {
            // Pulisci il localStorage al logout
            localStorage.removeItem('notificationCount');
            localStorage.removeItem('lastNotificationReset');
        });
    }

    // Inizializza WebSocket con il contesto dell'applicazione
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const contextPath = window.location.pathname.split('/')[1];
    const wsUrl = `${wsProtocol}//${window.location.host}/${contextPath}/notifications?userType=${userType}&userId=${userId}`;
    console.log('Connecting to WebSocket:', wsUrl);
    
    try {
        const ws = new WebSocket(wsUrl);

        // Trova il container appropriato
        const notificationContainer = document.getElementById(
            userType === 'MENTEE' ? 'menteeNotifications' : 'mentorNotifications'
        );
        
        if (notificationContainer) {
            console.log('Found notification container:', notificationContainer.id);
            // Aggiungiamo il messaggio "Nessuna notifica" se il container è vuoto
            if (notificationContainer.children.length === 0) {
                const emptyMessage = document.createElement('div');
                emptyMessage.className = 'empty-message';
                emptyMessage.textContent = 'Nessuna notifica';
                notificationContainer.appendChild(emptyMessage);
            }
        } else {
            console.error('Notification container not found for type:', userType);
            return;
        }

        ws.onmessage = function(event) {
            console.log('RAW WebSocket message received:', event.data);
            try {
                const data = JSON.parse(event.data);
                console.log('Parsed notification data:', data);

                // Controlla se la notifica è più recente dell'ultimo reset
                const lastReset = localStorage.getItem('lastNotificationReset');
                const currentTime = new Date().getTime();
                
                if (!lastReset || currentTime > parseInt(lastReset)) {
                    // Incrementa il contatore e mostra il badge
                    notificationCount++;
                    localStorage.setItem('notificationCount', notificationCount.toString());
                    
                    if (notificationBadge) {
                        notificationBadge.textContent = notificationCount;
                        notificationBadge.style.display = 'flex';
                    }
                }
                
                const container = document.getElementById(
                    data.type === 'MENTEE' ? 'menteeNotifications' : 'mentorNotifications'
                );

                if (container) {
                    console.log('Creating notification in container:', container.id);
                    const notification = document.createElement('div');
                    notification.className = 'notification-item unread';
                    notification.textContent = data.message;
                    
                    // Rimuovi il messaggio "Nessuna notifica" se presente
                    const emptyMessage = container.querySelector('.empty-message');
                    if (emptyMessage) {
                        emptyMessage.remove();
                    }
                    
                    // Inserisci la nuova notifica in cima
                    container.insertBefore(notification, container.firstChild);
                    
                    // Mostra il container
                    container.style.display = 'block';

                    // Nascondi la notifica dopo 5 secondi
                    setTimeout(() => {
                        notification.classList.remove('unread');
                    }, 5000);
                } else {
                    console.error('Container not found for notification type:', data.type);
                }
            } catch (error) {
                console.error('Error processing notification:', error, 'Raw data:', event.data);
            }
        };

        ws.onerror = function(error) {
            console.error('WebSocket Error:', error);
        };

        ws.onclose = function() {
            console.log('WebSocket Connection Closed');
            // Riprova a connettersi dopo 5 secondi
            setTimeout(initializeNotifications, 5000);
        };

        ws.onopen = function() {
            console.log('WebSocket Connection Established');
        };
    } catch (error) {
        console.error('Error initializing WebSocket:', error);
    }
}

// Prova a inizializzare immediatamente
initializeNotifications();

// Se fallisce, riprova quando il DOM è completamente caricato
document.addEventListener('DOMContentLoaded', function() {
    if (!document.querySelector('.header')) {
        console.log('Retrying initialization after DOMContentLoaded');
        initializeNotifications();
    }
});
