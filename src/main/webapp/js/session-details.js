document.addEventListener('DOMContentLoaded', function() {
    const giorni = ['Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato', 'Domenica'];
    let sessionData = null;

    // Recupera l'ID della sessione dall'URL
    const urlParams = new URLSearchParams(window.location.search);
    const sessioneId = urlParams.get('idSessione');

    // Funzione per caricare i dati della sessione
    async function loadSessionData() {
        try {
            const params = new URLSearchParams();
            params.append('sessioneId', sessioneId);

            const response = await fetch('GetSessionServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params
            });

            const data = await response.json();
            if (data.error) {
                throw new Error(data.error);
            }

            sessionData = data;

            // Aggiorna i dettagli della sessione nella pagina
            document.querySelector('.session-title').textContent = data.sessione.titolo;
            document.querySelector('.session-description').textContent = data.sessione.descrizione;
            document.querySelector('.session-price').textContent = `€${data.sessione.prezzo}`;

            if (data.sessione.immagine) {
                const img = document.querySelector('.session-image');
                img.src = data.sessione.immagine;
                img.alt = data.sessione.titolo;
            }

            // Mostra le sessioni correlate
            if (data.sessioniCorrelate && data.sessioniCorrelate.length > 0) {
                console.log('Sessioni correlate trovate:', data.sessioniCorrelate.length);
                const container = document.querySelector('.py-5 .row');
                if (!container) {
                    console.error('Container delle sessioni correlate non trovato');
                    return;
                }
                console.log('Container trovato, aggiungo le sessioni');

                data.sessioniCorrelate.forEach((sessione, index) => {
                    const desc = sessione.descrizione.length > 100 ?
                        sessione.descrizione.substring(0, 97) + '...' :
                        sessione.descrizione;

                    const card = `
                        <div class="col-md-3">
                            <a href="sessionDetails.jsp?idSessione=${sessione.idSessione}" class="card-link">
                                <div class="featured-card anim-delay-${Math.min(index, 3)}">
                                    <img src="${sessione.immagine}" class="card-img-top" alt="${sessione.titolo}">
                                    <div class="card-body">
                                        <h5 class="card-title">${sessione.titolo}</h5>
                                        <p class="card-text">${desc}</p>
                                        <p class="card-price">€${sessione.prezzo}</p>
                                    </div>
                                </div>
                            </a>
                        </div>
                    `;
                    container.innerHTML += card;
                });
            } else {
                console.log('Nessuna sessione correlata trovata');
            }

            // Inizializza il calendario
            initializeCalendar(data.timeslots);

        } catch (error) {
            console.error('Errore nel caricamento dei dati:', error);
            const container = document.querySelector('.session-container');
            container.innerHTML = `
                <div class="alert alert-danger" role="alert">
                    Errore nel caricamento dei dati della sessione: ${error.message}
                </div>`;
        }
    }

    // Funzione per inizializzare il calendario
    function initializeCalendar(timeslots) {
        flatpickr("#datePicker", {
            locale: "it",
            dateFormat: "Y-m-d",
            minDate: "today",
            enable: [
                function(date) {
                    const dayOfWeek = date.getDay();
                    const adjustedDay = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
                    return timeslots.some(slot => slot.giorno === adjustedDay);
                }
            ],
            onChange: function(selectedDates, dateStr) {
                if (selectedDates.length > 0) {
                    document.getElementById('selectedDate').value = dateStr;
                    fetchAvailableTimeslots(dateStr);
                }
            }
        });
    }

    function formatHour(hour) {
        const formattedHour = hour.toString().padStart(2, '0');
        const nextHour = (hour + 1).toString().padStart(2, '0');
        return `${formattedHour}:00 - ${nextHour}:00`;
    }

    function fetchAvailableTimeslots(date) {
        if (!sessionData) return;

        const container = document.getElementById('timeslots');
        container.innerHTML = '';

        const selectedDate = new Date(date);
        const dayOfWeek = selectedDate.getDay();
        const adjustedDay = dayOfWeek === 0 ? 6 : dayOfWeek - 1;

        const dayTimeslots = sessionData.timeslots
            .filter(slot => slot.giorno === adjustedDay)
            .sort((a, b) => a.orario - b.orario);

        if (dayTimeslots.length === 0) {
            container.innerHTML = '<div class="alert alert-info">Nessun timeslot disponibile per questo giorno</div>';
            return;
        }

        const timeslotsContainer = document.getElementById('timeslotsContainer');
        timeslotsContainer.classList.remove('d-none');
        timeslotsContainer.classList.add('show');

        dayTimeslots.forEach(slot => {
            const params = new URLSearchParams();
            params.append('action', 'checkAvailability');
            params.append('timeslotId', slot.idTimeslot);
            params.append('date', date);

            fetch('BookingServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params
            })
                .then(response => response.json())
                .then(availability => {
                    if (availability.error) {
                        throw new Error(availability.error);
                    }

                    const div = document.createElement('div');
                    let className = 'time-slot';
                    let isBookable = true;
                    
                    if (!availability.disponibile) {
                        if (availability.status === 'IN_ATTESA') {
                            className += ' pending';
                            div.title = 'Prenotazione in attesa di pagamento';
                        } else {
                            className += ' unavailable';
                            div.title = 'Timeslot non disponibile';
                        }
                        isBookable = false;
                    }
                    
                    div.className = className;
                    div.textContent = formatHour(slot.orario);

                    if (isBookable) {
                        div.addEventListener('click', () => selectTimeslot(div, slot.idTimeslot, slot.orario));
                    }

                    container.appendChild(div);
                })
                .catch(error => {
                    console.error('Errore nel controllo disponibilità:', error);
                    const errorDiv = document.createElement('div');
                    errorDiv.className = 'alert alert-danger';
                    errorDiv.textContent = `Errore nel controllo disponibilità per le ${formatHour(slot.orario)}`;
                    container.appendChild(errorDiv);
                });
        });
    }

    function selectTimeslot(element, timeslotId, orario) {
        document.querySelectorAll('.time-slot.selected').forEach(el => {
            el.classList.remove('selected');
        });

        element.classList.add('selected');
        document.getElementById('selectedTimeslot').value = timeslotId;

        const bookButton = document.getElementById('bookButton');
        bookButton.disabled = false;
        bookButton.textContent = `Prenota per le ${formatHour(orario)}`;
    }

    // Gestione del form di prenotazione
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        bookingForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const selectedDate = document.getElementById('selectedDate').value;
            const selectedTimeslot = document.getElementById('selectedTimeslot').value;

            if (!selectedDate || !selectedTimeslot) {
                alert('Seleziona una data e un orario per procedere con la prenotazione.');
                return;
            }

            // Verifica se l'utente è loggato
            const idUtente = document.getElementById('idUtente')?.value;
            if (!idUtente) {
                alert('Devi effettuare il login per prenotare una sessione.');
                window.location.href = 'login.jsp';
                return;
            }

            const bookButton = document.getElementById('bookButton');
            bookButton.disabled = true;
            bookButton.textContent = 'Creazione prenotazione in corso...';

            try {
                // Crea la prenotazione
                const bookingParams = new URLSearchParams({
                    action: 'create',
                    idSessione: sessioneId,
                    timeslotId: selectedTimeslot,
                    dataPrenotazione: selectedDate,
                    idUtente: idUtente
                });

                const bookingResponse = await fetch('BookingServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: bookingParams
                });

                const bookingResult = await bookingResponse.json();

                if (!bookingResult.success) {
                    throw new Error(bookingResult.error || 'Errore durante la creazione della prenotazione');
                }

                // Reindirizza alla pagina di pagamento
                window.location.href = `payment.jsp?idPrenotazione=${bookingResult.idPrenotazione}&totale=${sessionData.sessione.prezzo}`;

            } catch (error) {
                console.error('Errore durante la prenotazione:', error);
                alert('Si è verificato un errore durante la prenotazione: ' + error.message);
                bookButton.disabled = false;
                bookButton.textContent = 'Prenota Sessione';
            }
        });
    }

    // Carica i dati della sessione all'avvio
    loadSessionData();
});