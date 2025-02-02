<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:if test="${sessionScope.ruolo != 'MENTOR'}">
    <c:redirect url="index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="it" class="h-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>Mentor Dashboard - EasyCoach</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/mentor.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/dashboard.css" rel="stylesheet">
    <style>
        #editSessionModal .timeslot-grid {
            display: grid;
            grid-template-columns: repeat(7, minmax(60px, 1fr));
            gap: 8px;
            margin: 20px auto;
            max-width: 90%;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 8px;
        }

        #editSessionModal .timeslot-cell {
            padding: 8px;
            border-radius: 4px;
            text-align: center;
            cursor: pointer;
            font-size: 0.9em;
            transition: transform 0.1s ease-in-out;
        }

        #editSessionModal .timeslot-cell:hover {
            transform: scale(1.05);
        }
    </style>
</head>
<body class="d-flex flex-column h-100">
<jsp:include page="WEB-INF/header.jsp" />

<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <nav class="col-md-2 d-md-block bg-light sidebar">
            <div class="position-sticky">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="#" id="showProfile">
                            <i class="bi bi-person-circle"></i> Gestione Profilo
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" id="showSessions">
                            <i class="bi bi-calendar-event"></i> Gestione Sessioni
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" id="showBookings">
                            <i class="bi bi-book"></i> Prenotazioni Attive
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Contenuto principale -->
        <main class="col-md-10 ms-sm-auto px-md-4">
            <!-- Sezione Profilo -->
            <div id="profileSection">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Gestione Profilo</h1>
                </div>

                <% if (request.getAttribute("errore") != null) { %>
                <div class="alert alert-danger">
                    <%= request.getAttribute("errore") %>
                </div>
                <% } %>

                <% if (session.getAttribute("messaggio") != null) { %>
                <div class="alert alert-success">
                    <%= session.getAttribute("messaggio") %>
                    <% session.removeAttribute("messaggio"); %>
                </div>
                <% } %>

                <div class="row justify-content-center">
                    <div class="col-md-6 col-lg-5 col-xl-4">
                        <div class="profile-form-container">
                            <h5 class="mb-4">Modifica Password</h5>
                            <form id="profileForm" action="${pageContext.request.contextPath}/UpdatePassword" method="post" onsubmit="return validatePasswordForm(event)">
                                <div class="mb-3">
                                    <label for="email" class="form-label">
                                        <i class="bi bi-envelope me-2"></i>Email
                                    </label>
                                    <input type="email" class="form-control border-0 bg-light" id="email" name="email" value="${sessionScope.utente.email}" readonly>
                                </div>
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">
                                        <i class="bi bi-key me-2"></i>Password Attuale
                                    </label>
                                    <input type="password" class="form-control border-0 bg-light" id="currentPassword" name="currentPassword" required>
                                    <span class="error-message"></span>
                                </div>
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">
                                        <i class="bi bi-key-fill me-2"></i>Nuova Password
                                    </label>
                                    <input type="password" class="form-control border-0 bg-light" id="newPassword" name="newPassword" required>
                                    <span class="error-message"></span>
                                </div>
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">
                                        <i class="bi bi-check2-circle me-2"></i>Conferma Password
                                    </label>
                                    <input type="password" class="form-control border-0 bg-light" id="confirmPassword" name="confirmPassword" required>
                                    <span class="error-message"></span>
                                </div>
                                <div class="d-flex gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-check-lg me-2"></i>Aggiorna Password
                                    </button>
                                    <button type="button" class="btn btn-danger" onclick="confirmDelete()">
                                        <i class="bi bi-trash me-2"></i>Elimina Account
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal di conferma eliminazione -->
            <div id="deleteModal" class="modal fade" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" style="color: white">Conferma Eliminazione</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Sei sicuro di voler eliminare il tuo account? Questa azione non può essere annullata.</p>
                            <p class="text-danger">Nota: Tutte le tue prenotazioni future verranno cancellate.</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annulla</button>
                            <button type="button" class="btn btn-danger" onclick="deleteAccount()">Elimina</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Sezione Sessioni  -->
            <div id="sessionsSection" style="display: none;">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Gestione Sessioni</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/session.jsp" class="btn btn-primary">
                            <i class="bi bi-plus-circle"></i> Nuova Sessione
                        </a>
                    </div>
                </div>
                
                <!-- Container per le sessioni caricate via AJAX -->
                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    <!-- Il contenuto verrà caricato dinamicamente via JavaScript -->
                </div>
            </div>

            <!-- Sezione Prenotazioni -->
            <div id="bookingsSection" style="display: none;">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Prenotazioni Attive</h1>
                </div>

                <div id="activeBookingsContainer">
                    <div id="activeBookings" class="row g-4">
                        <!-- Le prenotazioni verranno inserite qui dinamicamente -->
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<!-- Modal Modifica Sessione -->
<div class="modal fade" id="editSessionModal" tabindex="-1" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header" style="background-color: #1a3b5d; color: white;">
                <h5 class="modal-title">
                    <i class="bi bi-pencil-square me-2"></i>Modifica Sessione
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <form id="editSessionForm" class="needs-validation" novalidate>
                    <input type="hidden" id="editSessionId" name="idSessione">

                    <!-- Nome Sessione -->
                    <div class="mb-4">
                        <label for="editNome" class="form-label fw-bold">
                            <i class="bi bi-type-h1 me-2"></i>Nome Sessione
                        </label>
                        <input type="text" class="form-control form-control-lg border-0 bg-light"
                               id="editNome" name="nome" placeholder="Inserisci il nome della sessione">
                        <div id="editNome-error" class="text-danger mt-1 small"></div>
                    </div>

                    <!-- Descrizione -->
                    <div class="mb-4">
                        <label for="editDescrizione" class="form-label fw-bold">
                            <i class="bi bi-text-paragraph me-2"></i>Descrizione
                        </label>
                        <textarea class="form-control border-0 bg-light" id="editDescrizione"
                                  name="descrizione" rows="4"
                                  placeholder="Descrivi la tua sessione di coaching"></textarea>
                        <div id="editDescrizione-error" class="text-danger mt-1 small"></div>
                    </div>

                    <!-- Prezzo -->
                    <div class="mb-4">
                        <label for="editPrezzo" class="form-label fw-bold">
                            <i class="bi bi-currency-euro me-2"></i>Prezzo
                        </label>
                        <div class="input-group">
                            <input type="number" class="form-control form-control-lg border-0 bg-light"
                                   id="editPrezzo" name="prezzo" min="0" max="999" step="0.01"
                                   placeholder="Inserisci il prezzo della sessione">
                            <span class="input-group-text border-0 bg-light">€</span>
                        </div>
                        <div id="editPrezzo-error" class="text-danger mt-1 small"></div>
                    </div>

                    <!-- Immagine -->
                    <div class="mb-4">
                        <label for="editImmagine" class="form-label fw-bold">
                            <i class="bi bi-image me-2"></i>Immagine
                        </label>
                        <div class="position-relative">
                            <input type="file" class="form-control form-control-lg border-0 bg-light"
                                   id="editImmagine" name="immagine" accept="image/*">
                            <div id="editImagePreview" class="mt-3 d-none text-center">
                                <img src="" alt="Preview" class="img-thumbnail rounded shadow-sm"
                                     style="max-height: 200px;">
                            </div>
                        </div>
                        <input type="hidden" id="editImmagineAttuale" name="immagineAttuale">
                        <div id="editImmagine-error" class="text-danger mt-1 small"></div>
                    </div>

                    <!-- Timeslot Grid Container -->
                    <div class="mb-4">
                        <label class="form-label fw-bold">
                            <i class="bi bi-calendar-week me-2"></i>Disponibilità Settimanale
                        </label>
                        <div class="d-flex justify-content-center align-items-center">
                            <div class="timeslot-wrapper" style="width: 100%; max-width: 800px;">
                                <div id="editTimeSlotGrid"></div>
                            </div>
                        </div>
                        <div id="editTimeSlotGrid-error" class="text-danger mt-1 small"></div>
                    </div>


                </form>
            </div>
            <div class="modal-footer border-0">
                <button type="button" class="btn btn-lg btn-outline-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-lg me-2"></i>Chiudi
                </button>
                <button type="button" class="btn btn-lg btn-primary" onclick="saveSessionChanges()">
                    <i class="bi bi-check-lg me-2"></i>Salva Modifiche
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/profile-validation.js"></script>
<script src="${pageContext.request.contextPath}/js/mentor-dashboard.js"></script>
<script src="${pageContext.request.contextPath}/js/session-edit.js"></script>

</body>
</html>