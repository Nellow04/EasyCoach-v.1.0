document.addEventListener('DOMContentLoaded', function() {
    // Gestione della navigazione
    const showUsersLink = document.getElementById('showUsers');
    const showSessionsLink = document.getElementById('showSessions');
    const usersSection = document.getElementById('usersSection');
    const sessionsSection = document.getElementById('sessionsSection');

    showUsersLink.addEventListener('click', function(e) {
        e.preventDefault();
        fadeOut(sessionsSection, () => {
            usersSection.style.display = 'block';
            sessionsSection.style.display = 'none';
            fadeIn(usersSection);
            showUsersLink.classList.add('active');
            showSessionsLink.classList.remove('active');
            loadUsers();
        });
    });

    showSessionsLink.addEventListener('click', function(e) {
        e.preventDefault();
        fadeOut(usersSection, () => {
            usersSection.style.display = 'none';
            sessionsSection.style.display = 'block';
            fadeIn(sessionsSection);
            showSessionsLink.classList.add('active');
            showUsersLink.classList.remove('active');
            loadSessions();
        });
    });

    // Funzioni di animazione
    function fadeOut(element, callback) {
        element.style.opacity = 1;
        (function fade() {
            if ((element.style.opacity -= .1) < 0) {
                element.style.display = 'none';
                if (callback) callback();
            } else {
                requestAnimationFrame(fade);
            }
        })();
    }

    function fadeIn(element, display = 'block') {
        element.style.opacity = 0;
        element.style.display = display;
        (function fade() {
            let val = parseFloat(element.style.opacity);
            if (!((val += .1) > 1)) {
                element.style.opacity = val;
                requestAnimationFrame(fade);
            }
        })();
    }

    // Funzione per eliminare un utente
    function deleteUser(userId) {
        if (confirm('Sei sicuro di voler eliminare questo utente?')) {
            fetch(`${contextPath}/AdminServlet?action=deleteUser&userId=${userId}`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Errore nella risposta del server: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        loadUsers();
                        alert('Utente eliminato con successo');
                    } else {
                        alert('Errore durante l\'eliminazione dell\'utente: ' + data.error);
                    }
                })
                .catch(error => {
                    console.error('Errore:', error);
                    alert('Errore durante l\'eliminazione dell\'utente');
                });
        }
    }

    // Funzione per eliminare una sessione
    function deleteSession(sessionId) {
        if (confirm('Sei sicuro di voler eliminare questa sessione?')) {
            fetch(`${contextPath}/AdminServlet?action=deleteSession&sessionId=${sessionId}`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Errore nella risposta del server: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        loadSessions();
                        alert('Sessione eliminata con successo');
                    } else {
                        alert('Errore durante l\'eliminazione della sessione: ' + data.error);
                    }
                })
                .catch(error => {
                    console.error('Errore:', error);
                    alert('Errore durante l\'eliminazione della sessione');
                });
        }
    }

    // Variabili per memorizzare i dati originali
    let allUsers = [];
    let allSessions = [];

    // Event listeners per i filtri utenti
    document.getElementById('userSearchInput').addEventListener('input', filterUsers);
    document.getElementById('userRoleFilter').addEventListener('change', filterUsers);
    document.getElementById('userSearchBtn').addEventListener('click', filterUsers);

    // Event listeners per i filtri sessioni
    document.getElementById('sessionSearchInput').addEventListener('input', filterSessions);
    document.getElementById('sessionStatusFilter').addEventListener('change', filterSessions);
    document.getElementById('sessionPriceFilter').addEventListener('input', filterSessions);
    document.getElementById('sessionSearchBtn').addEventListener('click', filterSessions);

    // Funzione per filtrare gli utenti
    function filterUsers() {
        const searchTerm = document.getElementById('userSearchInput').value.toLowerCase();
        const roleFilter = document.getElementById('userRoleFilter').value;

        const filteredUsers = allUsers.filter(user => {
            const matchesSearch = user.nome.toLowerCase().includes(searchTerm) ||
                user.email.toLowerCase().includes(searchTerm);
            const matchesRole = !roleFilter || user.ruolo === roleFilter;
            return matchesSearch && matchesRole;
        });

        displayUsers(filteredUsers);
    }

    // Funzione per filtrare le sessioni
    function filterSessions() {
        const searchTerm = document.getElementById('sessionSearchInput').value.toLowerCase();
        const statusFilter = document.getElementById('sessionStatusFilter').value;
        const priceFilter = document.getElementById('sessionPriceFilter').value;

        const filteredSessions = allSessions.filter(session => {
            const matchesSearch = session.titolo.toLowerCase().includes(searchTerm);
            const matchesStatus = !statusFilter || session.statusSessione === statusFilter;
            const matchesPrice = !priceFilter || session.prezzo <= parseFloat(priceFilter);
            return matchesSearch && matchesStatus && matchesPrice;
        });

        displaySessions(filteredSessions);
    }

    // Funzione per visualizzare gli utenti filtrati
    function displayUsers(users) {
        const tbody = document.getElementById('usersTableBody');
        tbody.style.opacity = 0;
        tbody.innerHTML = '';

        if (users.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center">
                        Nessun utente trovato
                    </td>
                </tr>
            `;
        } else {
            users.forEach(user => {
                tbody.innerHTML += `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.nome}</td>
                        <td>${user.email}</td>
                        <td>${user.ruolo}</td>
                        <td>
                            <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">
                                <i class="bi bi-trash"></i> Elimina
                            </button>
                        </td>
                    </tr>
                `;
            });
        }

        fadeIn(tbody, 'table-row-group');
    }

    // Funzione per visualizzare le sessioni filtrate
    function displaySessions(sessions) {
        const grid = document.getElementById('sessionsGrid');
        grid.style.opacity = 0;
        grid.innerHTML = '';

        if (sessions.length === 0) {
            grid.innerHTML = `
                <div class="alert alert-info text-center">
                    Nessuna sessione trovata
                </div>
            `;
        } else {
            const row = document.createElement('div');
            row.className = 'row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4';

            sessions.forEach(session => {
                const col = document.createElement('div');
                col.className = 'col';
                col.innerHTML = `
                    <div class="card h-100">
                        <img src="${session.immagine || 'img/placeholder.jpg'}" class="card-img-top" alt="Immagine sessione">
                        <div class="card-body">
                            <h5 class="card-title">${session.titolo}</h5>
                            <p class="card-text">${session.descrizione}</p>
                            <p class="card-text">
                                <small class="text-muted">Mentor: ${session.mentorNome}</small>
                            </p>
                            <p class="card-text">
                                <span class="badge ${session.statusSessione === 'ATTIVA' ? 'bg-success' : 'bg-secondary'}">
                                    ${session.statusSessione}
                                </span>
                                <span class="ms-2">€${session.prezzo}</span>
                            </p>
                            <button class="btn btn-danger" onclick="deleteSession(${session.idSessione})">
                                <i class="bi bi-trash"></i> Elimina
                            </button>
                        </div>
                    </div>
                `;
                row.appendChild(col);
            });

            grid.appendChild(row);
        }

        fadeIn(grid);
    }

    // Caricamento degli utenti
    function loadUsers() {
        console.log('Inizio caricamento utenti');

        fetch(contextPath + '/AdminServlet?action=getUsers', {
            method: 'POST',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Errore nella risposta del server: ' + response.status);
                }
                return response.json();
            })
            .then(users => {
                allUsers = users; // Memorizza tutti gli utenti
                displayUsers(users);
            })
            .catch(error => {
                console.error('Errore nel caricamento degli utenti:', error);
                const tbody = document.getElementById('usersTableBody');
                tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center text-danger">
                        Errore nel caricamento degli utenti. Riprova più tardi.
                        <br>
                        <small>${error.message}</small>
                    </td>
                </tr>
            `;
                fadeIn(tbody, 'table-row-group');
            });
    }

    // Caricamento delle sessioni
    function loadSessions() {
        console.log('Inizio caricamento sessioni');

        fetch(contextPath + '/AdminServlet?action=getSessions', {
            method: 'POST',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Errore nella risposta del server: ' + response.status);
                }
                return response.json();
            })
            .then(sessions => {
                allSessions = sessions; // Memorizza tutte le sessioni
                displaySessions(sessions);
            })
            .catch(error => {
                console.error('Errore nel caricamento delle sessioni:', error);
                const grid = document.getElementById('sessionsGrid');
                grid.innerHTML = `
                <div class="alert alert-danger">
                    Errore nel caricamento delle sessioni. Riprova più tardi.
                    <br>
                    <small>${error.message}</small>
                </div>
            `;
                fadeIn(grid);
            });
    }



    // Esponi le funzioni globalmente
    window.deleteUser = deleteUser;
    window.deleteSession = deleteSession;

    // Carica gli utenti all'avvio
    loadUsers();
});
