document.addEventListener('DOMContentLoaded', function() {
    // Elementi DOM
    const profileSection = document.getElementById('profileSection');
    const sessionsSection = document.getElementById('sessionsSection');
    const bookingsSection = document.getElementById('bookingsSection');

    // Gestione della navigazione
    document.getElementById('showProfile').addEventListener('click', function(e) {
        e.preventDefault();
        profileSection.style.display = 'block';
        sessionsSection.style.display = 'none';
        bookingsSection.style.display = 'none';
        updateActiveLink(this);
    });

    document.getElementById('showSessions').addEventListener('click', function(e) {
        e.preventDefault();
        profileSection.style.display = 'none';
        sessionsSection.style.display = 'block';
        bookingsSection.style.display = 'none';
        updateActiveLink(this);
        loadSessions(); // Carica le sessioni quando si visualizza la sezione
    });

    document.getElementById('showBookings').addEventListener('click', function(e) {
        e.preventDefault();
        profileSection.style.display = 'none';
        sessionsSection.style.display = 'none';
        bookingsSection.style.display = 'block';
        updateActiveLink(this);
        loadBookings();
    });

    // Carica le sessioni all'avvio
    loadSessions();
});

function updateActiveLink(clickedLink) {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    clickedLink.classList.add('active');
}

function loadSessions() {
    console.log('Caricamento sessioni...');
    console.log('URL:', contextPath + '/DashboardMentorServlet?action=getSessions');

    fetch(contextPath + '/DashboardMentorServlet?action=getSessions')
        .then(response => {
            console.log('Status:', response.status);
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.text().then(text => {
                console.log('Response text:', text);
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.error('Error parsing JSON:', e);
                    throw new Error('Invalid JSON response');
                }
            });
        })
        .then(data => {
            console.log('Dati ricevuti:', data);
            const sessionsContainer = document.querySelector('#sessionsSection .row');
            sessionsContainer.innerHTML = ''; // Pulisce il contenitore

            if (!data.sessions || data.sessions.length === 0) {
                sessionsContainer.innerHTML = `
                    <div class="col-12 text-center">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            Non hai ancora creato nessuna sessione
                        </div>
                    </div>`;
                return;
            }

            data.sessions.forEach(session => {
                const sessionCard = createSessionCard(session);
                sessionsContainer.appendChild(sessionCard);
            });
        })
        .catch(error => {
            console.error('Errore nel caricamento delle sessioni:', error);
            const sessionsContainer = document.querySelector('#sessionsSection .row');
            sessionsContainer.innerHTML = `
                <div class="col-12">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        Errore nel caricamento delle sessioni: ${error.message}
                    </div>
                </div>`;
        });
}

function createSessionCard(session) {
    const col = document.createElement('div');
    col.className = 'col';
    
    col.innerHTML = `
        <div class="card h-100" style="border: 2px solid var(--bs-primary)">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">${session.titolo}</h5>
                <span class="badge ${session.statusSessione === 'ATTIVA' ? 'bg-primary' : 'bg-secondary'}">
                    ${session.statusSessione}
                </span>
            </div>
            ${session.immagine ? `<img src="${contextPath}/${session.immagine}" class="card-img-top" alt="${session.titolo}">` : ''}
            <div class="card-body">
                <p class="card-text">${session.descrizione}</p>
                <p class="card-text">
                    <small class="text-muted">
                        <i class="bi bi-currency-euro me-2"></i>${session.prezzo.toFixed(2)}€
                    </small>
                </p>
            </div>
            <div class="card-footer bg-transparent border-0 d-flex justify-content-end gap-2">
                <button class="btn btn-primary btn-sm" onclick="openEditModal(${session.idSessione})">
                    <i class="bi bi-pencil-square me-1"></i>Modifica
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteSession(${session.idSessione})">
                    <i class="bi bi-trash me-1"></i>Elimina
                </button>
            </div>
        </div>`;
    
    return col;
}

function createBookingCard(booking) {
    const card = document.createElement('div');
    card.className = 'card h-100';
    card.style.border = '2px solid var(--bs-primary)';  // Usa il colore primary di Bootstrap
    
    // Header della card con titolo e badge di stato
    const cardHeader = document.createElement('div');
    cardHeader.className = 'card-header d-flex justify-content-between align-items-center';
    cardHeader.innerHTML = `
        <h5 class="card-title mb-0">${booking.titolo}</h5>
        <span class="badge bg-primary">Attiva</span>
    `;
    
    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';
    
    // Contenuto della card con icone
    const cardContent = document.createElement('div');
    cardContent.className = 'card-text';
    cardContent.innerHTML = `
        <p><i class="bi bi-person-circle me-2"></i>${booking.menteeName}</p>
        <p><i class="bi bi-clock me-2"></i>${booking.orario}</p>
        <p><i class="bi bi-calendar me-2"></i>${new Date(booking.dataPrenotazione).toLocaleDateString('it-IT')}</p>
    `;
    
    cardBody.appendChild(cardContent);
    
    // Link videoconferenza (se presente)
    if (booking.linkVideoconferenza) {
        const link = document.createElement('a');
        link.href = booking.linkVideoconferenza;
        link.className = 'btn btn-primary mt-3';
        link.innerHTML = '<i class="bi bi-camera-video me-2"></i>Partecipa alla sessione';
        link.target = '_blank';
        cardBody.appendChild(link);
    }
    
    card.appendChild(cardHeader);
    card.appendChild(cardBody);
    
    return card;
}

function loadBookings() {
    fetch(contextPath + '/DashboardMentorServlet?action=getBookings')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const activeContainer = document.getElementById('activeBookings');
            activeContainer.innerHTML = '';
            
            if (data.activeBookings.length === 0) {
                activeContainer.innerHTML = `
                    <div class="col-12">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            Non hai prenotazioni attive al momento.
                        </div>
                    </div>`;
            } else {
                data.activeBookings.forEach(booking => {
                    const col = document.createElement('div');
                    col.className = 'col-md-6 col-lg-4 mb-4';
                    col.appendChild(createBookingCard(booking));
                    activeContainer.appendChild(col);
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('activeBookings').innerHTML = `
                <div class="col-12">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        Si è verificato un errore nel caricamento delle prenotazioni.
                    </div>
                </div>`;
        });
}