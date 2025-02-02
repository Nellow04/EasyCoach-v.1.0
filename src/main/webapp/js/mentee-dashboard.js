// Gestione della navigazione
document.addEventListener('DOMContentLoaded', function() {
    // Inizializza i filtri
    initializeFilters();
    
    // Carica le prenotazioni all'avvio
    if (document.getElementById('bookingsSection').style.display !== 'none') {
        loadBookings();
    }

    // Event listeners per la navigazione
    document.getElementById('showProfile').addEventListener('click', function(e) {
        e.preventDefault();
        showSection('profileSection');
    });

    document.getElementById('showSearch').addEventListener('click', function(e) {
        e.preventDefault();
        showSection('searchSection');
    });

    document.getElementById('showBookings').addEventListener('click', function(e) {
        e.preventDefault();
        showSection('bookingsSection');
        loadBookings();
    });
});

// Inizializza i filtri delle prenotazioni
function initializeFilters() {
    const filterButtons = document.querySelectorAll('[data-filter]');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Rimuovi la classe active da tutti i bottoni
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Aggiungi la classe active al bottone cliccato
            this.classList.add('active');
            
            const filter = this.getAttribute('data-filter');
            applyFilter(filter);
        });
    });
}

// Applica il filtro selezionato
function applyFilter(filter) {
    const activeContainer = document.getElementById('activeBookingsContainer');
    const completedContainer = document.getElementById('completedBookingsContainer');

    switch(filter) {
        case 'active':
            activeContainer.style.display = 'block';
            completedContainer.style.display = 'none';
            break;
        case 'completed':
            activeContainer.style.display = 'none';
            completedContainer.style.display = 'block';
            break;
        default: // 'all'
            activeContainer.style.display = 'block';
            completedContainer.style.display = 'block';
    }
}

// Mostra la sezione selezionata
function showSection(sectionId) {
    document.getElementById('profileSection').style.display = 'none';
    document.getElementById('searchSection').style.display = 'none';
    document.getElementById('bookingsSection').style.display = 'none';
    document.getElementById(sectionId).style.display = 'block';

    // Aggiorna la classe active nella sidebar
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
    // Correggiamo il selettore per trovare il link corretto
    const linkId = 'show' + sectionId.charAt(0).toUpperCase() + sectionId.slice(1).replace('Section', '');
    const activeLink = document.getElementById(linkId);
    if (activeLink) {
        activeLink.classList.add('active');
    }
}

// Carica le prenotazioni
function loadBookings() {
    fetch(contextPath + '/DashboardMenteeServlet?action=getBookings')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const activeContainer = document.getElementById('activeBookings');
            const completedContainer = document.getElementById('completedBookings');
            
            // Pulisci i container
            activeContainer.innerHTML = '';
            completedContainer.innerHTML = '';
            
            // Prenotazioni attive
            if (data.activeBookings.length === 0) {
                activeContainer.innerHTML = `
                    <div class="col-12">
                        <div class="alert alert-info">
                            Non hai prenotazioni attive al momento.
                        </div>
                    </div>`;
            } else {
                data.activeBookings.forEach(booking => {
                    const col = document.createElement('div');
                    col.className = 'col-md-6 col-lg-4 mb-4';
                    col.appendChild(createBookingCard(booking, true));
                    activeContainer.appendChild(col);
                });
            }
            
            // Prenotazioni concluse
            if (data.completedBookings.length === 0) {
                completedContainer.innerHTML = `
                    <div class="col-12">
                        <div class="alert alert-info">
                            Non hai prenotazioni concluse.
                        </div>
                    </div>`;
            } else {
                data.completedBookings.forEach(booking => {
                    const col = document.createElement('div');
                    col.className = 'col-md-6 col-lg-4 mb-4';
                    col.appendChild(createBookingCard(booking, false));
                    completedContainer.appendChild(col);
                });
            }
        })
        .catch(error => {
            console.error('Errore nel caricamento delle prenotazioni:', error);
            const errorMessage = document.createElement('div');
            errorMessage.className = 'alert alert-danger';
            errorMessage.textContent = 'Si è verificato un errore nel caricamento delle prenotazioni.';
            document.getElementById('activeBookings').appendChild(errorMessage.cloneNode(true));
            document.getElementById('completedBookings').appendChild(errorMessage);
        });
}

function createBookingCard(booking, isActive) {
    const card = document.createElement('div');
    card.className = `card h-100 ${isActive ? 'border-primary' : 'border-secondary'}`;
    
    // Header della card con titolo e badge di stato
    const cardHeader = document.createElement('div');
    cardHeader.className = 'card-header d-flex justify-content-between align-items-center';
    cardHeader.innerHTML = `
        <h5 class="card-title mb-0">${booking.titolo}</h5>
        <span class="badge ${isActive ? 'bg-primary' : 'bg-secondary'}">
            ${isActive ? 'Attiva' : 'Conclusa'}
        </span>
    `;
    
    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';
    
    // Contenuto della card con icone
    const cardContent = document.createElement('div');
    cardContent.className = 'card-text';
    cardContent.innerHTML = `
        <p><i class="bi bi-person-circle me-2"></i>${booking.mentorName}</p>
        <p><i class="bi bi-clock me-2"></i>${booking.orario}</p>
        <p><i class="bi bi-calendar me-2"></i>${new Date(booking.dataPrenotazione).toLocaleDateString('it-IT')}</p>
    `;
    
    cardBody.appendChild(cardContent);
    
    // Link videoconferenza (se presente e la prenotazione è attiva)
    if (booking.linkVideoconferenza && isActive) {
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
