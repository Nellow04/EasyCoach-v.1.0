// Definisci le funzioni di validazione nell'oggetto window
window.validateTitle = function(title) {
    return title && title.length >= 2 && title.length <= 25;
};

window.validateDescription = function(description) {
    return description && description.length >= 2 && description.length <= 250;
};

window.validatePrice = function(price) {
    const numPrice = parseFloat(price);
    return !isNaN(numPrice) && numPrice > 0 && numPrice <= 999;
};

// Gestione preview immagine
document.getElementById('editImmagine').addEventListener('change', function() {
    const preview = document.getElementById('editImagePreview');
    const previewImg = preview.querySelector('img');

    if (this.files && this.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            preview.classList.remove('d-none');
        };
        reader.readAsDataURL(this.files[0]);
    } else {
        preview.classList.add('d-none');
        previewImg.src = '';
    }
});

// Funzione per mostrare errori nel form di modifica
function showEditError(elementId, message) {
    let errorElement = document.getElementById(`${elementId}-error`);
    if (!errorElement) {
        errorElement = document.createElement('div');
        errorElement.id = `${elementId}-error`;
        errorElement.className = 'text-danger mt-1';
        const inputElement = document.getElementById(elementId);
        if (inputElement) {
            inputElement.parentNode.appendChild(errorElement);
        }
    }
    errorElement.textContent = message;
}

// Funzione per rimuovere errori nel form di modifica
function clearEditError(elementId) {
    const errorElement = document.getElementById(`${elementId}-error`);
    if (errorElement) {
        errorElement.textContent = '';
    }
}

// Funzione per validare il form di modifica
function validateEditForm() {
    const nome = document.getElementById('editNome').value;
    const descrizione = document.getElementById('editDescrizione').value;
    const prezzo = document.getElementById('editPrezzo').value;

    let isValid = true;

    if (!window.validateTitle(nome)) {
        showEditError('editNome', 'Il titolo deve essere tra 2 e 25 caratteri');
        isValid = false;
    } else {
        clearEditError('editNome');
    }

    if (!window.validateDescription(descrizione)) {
        showEditError('editDescrizione', 'La descrizione deve essere tra 2 e 250 caratteri');
        isValid = false;
    } else {
        clearEditError('editDescrizione');
    }

    if (!window.validatePrice(prezzo)) {
        showEditError('editPrezzo', 'Il prezzo deve essere maggiore di 0 e non superiore a 999€');
        isValid = false;
    } else {
        clearEditError('editPrezzo');
    }

    // Controlla se c'è una nuova immagine o se ne esiste già una
    const imageInput = document.getElementById('editImmagine');
    const immagineAttuale = document.getElementById('editImmagineAttuale').value;

    if (!imageInput.files?.length && !immagineAttuale) {
        showEditError('editImmagine', 'Seleziona un\'immagine');
        isValid = false;
    } else {
        clearEditError('editImmagine');
    }

    if (!validateEditTimeslots()) {
        showEditError('editTimeSlotGrid', 'Seleziona almeno un timeslot');
        isValid = false;
    } else {
        clearEditError('editTimeSlotGrid');
    }

    // Aggiorna lo stato del pulsante di conferma
    const confirmButton = document.querySelector('#editSessionModal .btn-primary');
    if (confirmButton) {
        confirmButton.disabled = !isValid;
    }

    return isValid;
}

// Funzione per aprire il modal di modifica
async function openEditModal(sessionId) {
    try {
        console.log('%c[Edit] Apertura modal per sessione: ' + sessionId, 'color: blue; font-weight: bold');

        const data = new URLSearchParams();
        data.append('action', 'load');
        data.append('idSessione', sessionId);

        const response = await fetch(contextPath + '/EditSessionServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: data
        });

        const dataSessione = await response.json();
        console.log('[Edit] Risposta completa dal server:', JSON.stringify(dataSessione, null, 2));
        console.log('[Edit] Proprietà dell\'oggetto:', Object.keys(dataSessione));
        console.log('[Edit] Status della risposta:', response.status);

        if (!dataSessione.sessione) {
            console.error('[Edit] Errore nei dati della sessione: Sessione non trovata');
            throw new Error('Sessione non trovata');
        }

        console.log('[Edit] Dati sessione ricevuti:', dataSessione);

        // Popola il form con tutti i campi
        document.getElementById('editSessionId').value = sessionId;
        document.getElementById('editNome').value = dataSessione.sessione.titolo;
        document.getElementById('editDescrizione').value = dataSessione.sessione.descrizione;
        document.getElementById('editPrezzo').value = dataSessione.sessione.prezzo;

        // Gestione immagine esistente
        const preview = document.getElementById('editImagePreview');
        const previewImg = preview.querySelector('img');
        document.getElementById('editImmagineAttuale').value = dataSessione.sessione.immagine;

        if (dataSessione.sessione.immagine) {
            previewImg.src = dataSessione.sessione.immagine;
            preview.classList.remove('d-none');
        } else {
            preview.classList.add('d-none');
            previewImg.src = '';
        }

        // Genera la griglia dei timeslot
        await generateEditTimeSlotGrid('editTimeSlotGrid', dataSessione.timeslots);

        const modal = new bootstrap.Modal(document.getElementById('editSessionModal'));
        modal.show();

        // Aggiungi listener per la validazione in tempo reale
        const inputs = ['editNome', 'editDescrizione', 'editPrezzo', 'editImmagine'];
        inputs.forEach(id => {
            document.getElementById(id).addEventListener('input', validateEditForm);
        });

        // Valida il form inizialmente
        validateEditForm();
    } catch (error) {
        console.error('[Edit] Errore:', error);
        alert('Errore durante il caricamento della sessione: ' + error.message);
    }
}

async function saveSessionChanges() {
    if (!validateEditForm()) {
        return;
    }

    try {
        const form = document.getElementById('editSessionForm');
        const formData = new FormData(form);

        // Aggiungi l'azione
        formData.append('action', 'save');

        // Rimuovi l'immagine dal FormData se non è stata selezionata una nuova
        const imageInput = document.getElementById('editImmagine');
        if (!imageInput.files || imageInput.files.length === 0) {
            formData.delete('immagine');
        }

        // Aggiungi i timeslot selezionati
        const selectedSlots = document.querySelectorAll('#editTimeSlotGrid input[type="checkbox"]:checked');
        selectedSlots.forEach(slot => {
            formData.append('timeslot_day[]', slot.dataset.day);
            formData.append('timeslot_hour[]', slot.dataset.hour);
        });

        const response = await fetch(contextPath + '/EditSessionServlet', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Errore durante il salvataggio');
        }

        // Chiudi il modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('editSessionModal'));
        modal.hide();

        // Salva un flag nella sessionStorage per mostrare il messaggio dopo il reload
        sessionStorage.setItem('showSuccessMessage', 'true');
        
        // Ricarica la pagina
        location.reload();
    } catch (error) {
        console.error('[Edit] Errore:', error);
        alert('Errore durante il salvataggio della sessione: ' + error.message);
    }
}

async function generateEditTimeSlotGrid(containerId, selectedTimeslots) {
    console.log('[Edit] Generazione griglia timeslot');
    const container = document.getElementById(containerId);
    if (!container) {
        console.error('[Edit] Container griglia non trovato:', containerId);
        return;
    }

    // Carica i timeslot occupati
    let occupiedSlots = new Set();
    try {
        const response = await fetch('CheckTimeSlotServlet');
        if (response.ok) {
            const data = await response.json();
            occupiedSlots = new Set(
                data.map(slot => `${slot.giorno}-${slot.orario}`)
            );
        }
    } catch (error) {
        console.error('[Edit] Errore caricamento timeslot occupati:', error);
    }

    // Crea la tabella
    const table = document.createElement('table');
    table.className = 'table table-bordered timeslot-table';

    // Crea l'header della tabella
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    const headers = ['Orario', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab', 'Dom'];
    headers.forEach(text => {
        const th = document.createElement('th');
        th.textContent = text;
        th.className = 'text-center';
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Crea il corpo della tabella
    const tbody = document.createElement('tbody');

    // Converti i timeslot selezionati in un set
    const selectedSlots = new Set(
        selectedTimeslots.map(slot => `${slot.giorno}-${slot.orario}`)
    );

    // Genera le righe per ogni ora
    for (let hour = 7; hour <= 21; hour++) {
        const row = document.createElement('tr');

        // Colonna orario
        const timeCell = document.createElement('td');
        timeCell.textContent = `${hour}:00 - ${hour + 1}:00`;
        timeCell.className = 'text-center';
        row.appendChild(timeCell);

        // Colonne giorni (0 = Lunedì, 6 = Domenica)
        for (let day = 0; day < 7; day++) {
            const cell = document.createElement('td');
            cell.className = 'text-center';

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.className = 'timeslot-checkbox';
            checkbox.dataset.day = day;
            checkbox.dataset.hour = hour;

            const slotKey = `${day}-${hour}`;

            // Gestisci lo stato del checkbox
            if (selectedSlots.has(slotKey)) {
                checkbox.checked = true;
            } else if (occupiedSlots.has(slotKey)) {
                checkbox.disabled = true;
                cell.classList.add('bg-secondary', 'bg-opacity-25');
            }

            // Aggiungi event listener per la validazione
            checkbox.addEventListener('change', validateEditForm);

            cell.appendChild(checkbox);
            row.appendChild(cell);
        }

        tbody.appendChild(row);
    }

    table.appendChild(tbody);

    // Pulisci il container e aggiungi la nuova tabella
    container.innerHTML = '';
    container.appendChild(table);
}

async function openDeleteModal(sessionId) {
    // Crea il modal dinamicamente se non esiste
    let modal = document.getElementById('deleteSessionModal');
    if (!modal) {
        const modalHtml = `
            <div class="modal fade" id="deleteSessionModal" tabindex="-1" aria-labelledby="deleteSessionModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="deleteSessionModalLabel">Conferma Eliminazione</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Sei sicuro di voler eliminare questa sessione? Questa azione non può essere annullata.</p>
                            <p class="text-danger">Nota: Tutte le prenotazioni associate verranno cancellate.</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annulla</button>
                            <button type="button" class="btn btn-danger" id="confirmDeleteButton">Elimina</button>
                        </div>
                    </div>
                </div>
            </div>`;
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        modal = document.getElementById('deleteSessionModal');
    }

    // Mostra il modal
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();

    // Aggiungi l'event listener per il pulsante di conferma
    document.getElementById('confirmDeleteButton').onclick = async function() {
        try {
            const data = new URLSearchParams();
            data.append('action', 'delete');
            data.append('idSessione', sessionId);

            console.log('[Delete] Invio richiesta eliminazione');
            const response = await fetch(contextPath + '/EditSessionServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: data
            });

            if (response.ok) {
                console.log('[Delete] Eliminazione completata');
                // Salva un flag nella sessionStorage per mostrare il messaggio dopo il reload
                sessionStorage.setItem('showDeleteMessage', 'true');
                location.reload();
            } else {
                console.error('[Delete] Errore durante l\'eliminazione');
                alert('Non puoi eliminare questa sessione, ci sono delle prenotazioni ATTIVE');
            }
        } catch (error) {
            console.error('[Delete] Errore:', error);
            alert('Errore durante l\'eliminazione della sessione');
        } finally {
            bootstrapModal.hide();
        }
    };
}

// Sostituisci la vecchia funzione deleteSession con quella nuova
window.deleteSession = openDeleteModal;

function validateEditTimeslots() {
    const selectedSlots = document.querySelectorAll('#editTimeSlotGrid input[type="checkbox"]:checked');
    return selectedSlots.length > 0;
}

document.addEventListener('DOMContentLoaded', function () {
    // Event listener per il pulsante di modifica
    document.querySelectorAll('.edit-session-btn').forEach(button => {
        button.addEventListener('click', function () {
            const sessionId = this.dataset.id;
            if (sessionId) {
                openEditModal(sessionId);
            }
        });
    });

    // Event listener per il form di modifica
    document.getElementById('editSessionForm').addEventListener('submit', function (event) {
        event.preventDefault();
        saveSessionChanges();
    });

    // Controlla se c'è un flag nella sessionStorage per mostrare il messaggio
    if (sessionStorage.getItem('showSuccessMessage')) {
        // Crea e mostra il messaggio di successo
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success alert-dismissible fade show';
        alertDiv.innerHTML = `
            Sessione modificata con successo
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        // Inserisci l'alert all'inizio della sezione principale
        const mainSection = document.querySelector('main');
        mainSection.insertBefore(alertDiv, mainSection.firstChild);

        // Rimuovi il flag dalla sessionStorage
        sessionStorage.removeItem('showSuccessMessage');

        // Rimuovi l'alert dopo 3 secondi
        setTimeout(() => {
            alertDiv.remove();
        }, 3000);
    }

    // Controlla se c'è un flag per il messaggio di eliminazione
    if (sessionStorage.getItem('showDeleteMessage')) {
        // Crea e mostra il messaggio di successo
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success alert-dismissible fade show';
        alertDiv.innerHTML = `
            Sessione eliminata con successo
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        // Inserisci l'alert all'inizio della sezione principale
        const mainSection = document.querySelector('main');
        mainSection.insertBefore(alertDiv, mainSection.firstChild);

        // Rimuovi il flag dalla sessionStorage
        sessionStorage.removeItem('showDeleteMessage');

        // Rimuovi l'alert dopo 3 secondi
        setTimeout(() => {
            alertDiv.remove();
        }, 3000);
    }
});