// Regex per la validazione
window.TITLE_REGEX = /^.{2,25}$/;
window.DESCRIPTION_REGEX = /^.{2,250}$/;
window.PRICE_REGEX = /^(?!0*[.,]0*$)\d{1,3}(?:[.,]\d{1,2})?$/;

// Set globale per i timeslot selezionati
window.selectedTimeslots = new Set();

// Funzioni di validazione
window.validateTitle = function(value) {
    return window.TITLE_REGEX.test(value.trim());
}

window.validateDescription = function(value) {
    return window.DESCRIPTION_REGEX.test(value.trim());
}

window.validatePrice = function(value) {
    if (!window.PRICE_REGEX.test(value)) return false;
    const price = parseFloat(value.replace(',', '.'));
    return price > 0 && price <= 999;
}

window.validateImage = function() {
    const imageInput = document.getElementById('immagine');
    return imageInput.files && imageInput.files.length > 0;
}

window.validateTimeslots = function() {
    return window.selectedTimeslots.size > 0;
}

// Funzione per gestire il submit del form
window.handleFormSubmit = async function(event) {
    event.preventDefault();

    const form = document.getElementById('sessionForm');
    const formData = new FormData(form);

    try {
        const response = await fetch('CreateSessionServlet', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Errore durante la creazione della sessione');
        }

        const data = await response.json();

        if (data.success) {
            // Reindirizza alla dashboard del mentor
            window.location.href = contextPath + '/dashboardMentor.jsp';
        } else {
            alert(data.error || 'Errore durante la creazione della sessione');
        }
    } catch (error) {
        console.error('Errore:', error);
        alert('Errore durante la creazione della sessione');
    }

    return false;
};

document.addEventListener('DOMContentLoaded', function() {

    const form = document.getElementById('sessionForm');
    const timeSlotTable = document.getElementById('timeSlotTable');
    const selectedTimeslotsList = document.getElementById('selectedTimeslotsList');
    const submitButton = form.querySelector('button[type="submit"]');

    // Funzioni di validazione
    window.showError = function(elementId, message) {
        let errorElement = document.getElementById(`${elementId}-error`);
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.id = `${elementId}-error`;
            errorElement.className = 'text-danger mt-1';
            const inputElement = document.getElementById(elementId);
            if (inputElement) {
                inputElement.parentNode.appendChild(errorElement);
            } else {
                // Se l'elemento di input non esiste, aggiungi l'errore alla fine del form
                const form = document.getElementById('sessionForm');
                form.appendChild(errorElement);
            }
        }
        errorElement.textContent = message;
    }

    window.clearError = function(elementId) {
        const errorElement = document.getElementById(`${elementId}-error`);
        if (errorElement) {
            errorElement.textContent = '';
        }
    }

    window.updateSubmitButton = function() {
        const titolo = document.getElementById('titolo');
        const descrizione = document.getElementById('descrizione');
        const prezzo = document.getElementById('prezzo');

        const isValid =
            window.validateTitle(titolo.value) &&
            window.validateDescription(descrizione.value) &&
            window.validatePrice(prezzo.value) &&
            window.validateImage() &&
            window.validateTimeslots();

        submitButton.disabled = !isValid;
        submitButton.style.opacity = isValid ? '1' : '0.5';
    }

    // Preview immagine
    const imageInput = document.getElementById('immagine');
    const imagePreview = document.getElementById('imagePreview');

    if (imageInput && imagePreview) {
        const previewImg = imagePreview.querySelector('img');
        imageInput.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImg.src = e.target.result;
                    imagePreview.classList.remove('d-none');
                };
                reader.readAsDataURL(file);
            } else {
                imagePreview.classList.add('d-none');
                previewImg.src = '';
            }
            window.updateSubmitButton();
        });
    }

    // Validazione in tempo reale
    form.addEventListener('input', function(e) {
        const { id, value } = e.target;

        switch(id) {
            case 'titolo':
                if (!window.validateTitle(value)) {
                    window.showError(id, 'Il titolo deve essere lungo tra 2 e 25 caratteri');
                } else {
                    window.clearError(id);
                }
                break;
            case 'descrizione':
                if (!window.validateDescription(value)) {
                    window.showError(id, 'La descrizione deve essere lunga tra 2 e 250 caratteri');
                } else {
                    window.clearError(id);
                }
                break;
            case 'prezzo':
                if (!window.validatePrice(value)) {
                    window.showError(id, 'Inserisci un prezzo valido (massimo 999€)');
                } else {
                    window.clearError(id);
                }
                break;
        }

        window.updateSubmitButton();
    });

    // Gestione submit del form
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        if (submitButton.disabled) {
            return;
        }

        try {
            const formData = new FormData(this);
            const response = await fetch('CreateSessionServlet', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                // Mostra il modal
                const successModal = new bootstrap.Modal(document.getElementById('successModal'));
                successModal.show();

                // Dopo 2 secondi, reindirizza alla dashboard del mentor
                setTimeout(() => {
                    window.location.href = 'dashboardMentor.jsp';
                }, 2000);
                return;
            }

            // Gestione errori
            try {
                const data = await response.json();
                if (data.errors) {
                    data.errors.forEach(error => {
                        window.showError(error.field || 'form', error.message);
                    });
                }
            } catch (jsonError) {
                alert('Si è verificato un errore durante la creazione della sessione. Riprova più tardi.');
            }
        } catch (error) {
            console.error('Errore durante l\'invio del form:', error);
            if (error.name !== 'TypeError' || !error.message.includes('Failed to fetch')) {
                alert('Si è verificato un errore durante la creazione della sessione. Riprova più tardi.');
            }
        }
    });

    // Carica i timeslot occupati dal server
    window.loadOccupiedTimeslots = async function() {
        try {
            // Ottieni l'ID del mentor dalla sessione
            const mentorId = document.getElementById('mentorId')?.value;

            if (!mentorId) {
                console.warn('[Timeslot] ID mentor non disponibile');
                return;
            }

            const response = await fetch('CheckTimeSlotServlet');

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error('Errore nella risposta del server: ' + response.status + ' - ' + errorText);
            }

            const data = await response.json();

            if (!Array.isArray(data)) {
                console.error('[Timeslot] Formato dati non valido:', data);
                return;
            }

            // Set per una ricerca più efficiente
            const occupiedSlots = new Set(
                data.map(slot => `${slot.giorno}-${slot.orario}`)
            );

            // Disabilita i timeslot occupati
            let disabledCount = 0;
            let totalCount = 0;
            const checkboxes = document.querySelectorAll('input[type="checkbox"][name="timeslot"]');

            checkboxes.forEach(checkbox => {
                totalCount++;
                const slotKey = `${checkbox.dataset.day}-${checkbox.dataset.hour}`;

                if (occupiedSlots.has(slotKey)) {
                    checkbox.disabled = true;
                    checkbox.checked = false;
                    checkbox.parentElement.classList.add('bg-secondary', 'bg-opacity-25');
                    disabledCount++;
                } else {
                    checkbox.disabled = false;
                    checkbox.parentElement.classList.remove('bg-secondary', 'bg-opacity-25');
                }
            });
        } catch (error) {
            // Mostra un messaggio di errore all'utente
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-danger mt-3';
            alertDiv.textContent = 'Errore nel caricamento degli orari: ' + error.message;
            timeSlotTable.parentElement.insertBefore(alertDiv, timeSlotTable);
        }
    }

    // Genera la griglia dei timeslot
    window.generateTimeSlotGrid = function() {
        if (!timeSlotTable) {
            console.log('Tabella timeslot non trovata, skip generazione');
            return;
        }

        const tbody = timeSlotTable.querySelector('tbody');
        if (!tbody) {
            //console.warn('Body della tabella non trovato');
            return;
        }

        tbody.innerHTML = '';
        let checkboxCount = 0;

        //Crea gli orari dalle 7 alle 21
        for (let hour = 7; hour <= 21; hour++) {
            const row = document.createElement('tr');

            // Colonna orario
            const timeCell = document.createElement('td');
            timeCell.textContent = `${hour}:00 - ${hour + 1}:00`;
            row.appendChild(timeCell);

            // Colonne giorni (0 = Lunedì, 6 = Domenica)
            for (let day = 0; day < 7; day++) {
                const cell = document.createElement('td');
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = 'timeslot';
                checkbox.className = 'timeslot-checkbox';
                checkbox.dataset.day = day;
                checkbox.dataset.hour = hour;
                checkboxCount++;

                cell.appendChild(checkbox);
                row.appendChild(cell);
            }

            tbody.appendChild(row);
        }
    }

    // Aggiorna la lista dei timeslot selezionati
    window.updateSelectedTimeslots = function() {
        if (!selectedTimeslotsList || !form) {
            return;
        }

        selectedTimeslotsList.innerHTML = '';
        const sortedTimeslots = Array.from(window.selectedTimeslots)
            .map(slot => {
                const [day, hour] = slot.split('-');
                return { day: parseInt(day), hour: parseInt(hour) };
            })
            .sort((a, b) => {
                if (a.day !== b.day) return a.day - b.day;
                return a.hour - b.hour;
            });

        const giorni = ['Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato', 'Domenica'];

        sortedTimeslots.forEach(slot => {
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.textContent = `${giorni[slot.day]} ${slot.hour}:00 - ${slot.hour + 1}:00`;

            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn btn-danger btn-sm';
            deleteBtn.textContent = 'Rimuovi';
            deleteBtn.onclick = () => {
                const checkbox = document.querySelector(`input[data-day="${slot.day}"][data-hour="${slot.hour}"]`);
                if (checkbox) checkbox.checked = false;
                window.selectedTimeslots.delete(`${slot.day}-${slot.hour}`);
                window.updateSelectedTimeslots();
                window.updateSubmitButton();
            };

            li.appendChild(deleteBtn);
            selectedTimeslotsList.appendChild(li);
        });

        // Aggiungi campi hidden per il form
        const existingInputs = form.querySelectorAll('input[name^="timeslot_"]');
        existingInputs.forEach(input => input.remove());

        sortedTimeslots.forEach(slot => {
            const dayInput = document.createElement('input');
            dayInput.type = 'hidden';
            dayInput.name = 'timeslot_day[]';
            dayInput.value = slot.day;
            form.appendChild(dayInput);

            const hourInput = document.createElement('input');
            hourInput.type = 'hidden';
            hourInput.name = 'timeslot_hour[]';
            hourInput.value = slot.hour;
            form.appendChild(hourInput);
        });

        // Controlla se ci sono timeslot selezionati e aggiorna il pulsante di submit e il messaggio di errore
        if (window.selectedTimeslots.size === 0) {
            window.showError('timeslots', 'Seleziona almeno un timeslot');
            submitButton.disabled = true;
        } else {
            window.clearError('timeslots');
            submitButton.disabled = false;
        }
    }

    // Gestione timeslots
    if (timeSlotTable) {
        window.generateTimeSlotGrid();
        window.loadOccupiedTimeslots();

        timeSlotTable.addEventListener('change', function(e) {
            if (e.target.classList.contains('timeslot-checkbox')) {
                const day = e.target.dataset.day;
                const hour = e.target.dataset.hour;
                const slotKey = `${day}-${hour}`;

                if (e.target.checked) {
                    window.selectedTimeslots.add(slotKey);
                } else {
                    window.selectedTimeslots.delete(slotKey);
                }

                window.updateSelectedTimeslots();
                window.updateSubmitButton();

                if (window.selectedTimeslots.size === 0) {
                    window.showError('timeslots', 'Seleziona almeno un timeslot');
                } else {
                    window.clearError('timeslots');
                }
            }
        });
    }
});