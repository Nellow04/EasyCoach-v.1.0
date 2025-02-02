<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${sessionScope.ruolo != 'MENTEE'}">
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
    <title>Mentee Dashboard - EasyCoach</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/dashboard.css" rel="stylesheet">
</head>
<body class="d-flex flex-column h-100">
<jsp:include page="WEB-INF/header.jsp" />

<div class="container-fluid mt-4">
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
                        <a class="nav-link" href="#" id="showSearch">
                            <i class="bi bi-search"></i> Ricerca Sessioni
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" id="showBookings">
                            <i class="bi bi-book"></i> Prenotazioni
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
                                    <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">
                                        <i class="bi bi-trash me-2"></i>Elimina Account
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Sezione Ricerca -->
            <div id="searchSection" style="display: none;">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Ricerca Sessioni</h1>
                </div>
                <div class="text-center">
                    <a href="${pageContext.request.contextPath}/search.jsp" class="btn btn-primary btn-lg">
                        <i class="bi bi-search me-2"></i>Vai alla Ricerca Sessioni
                    </a>
                </div>
            </div>

            <!-- Sezione Prenotazioni -->
            <div id="bookingsSection" style="display: none;">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Le Mie Prenotazioni</h1>
                </div>

                <!-- Filtri -->
                <div class="mb-4">
                    <div class="btn-group" role="group" aria-label="Filtri prenotazioni">
                        <button type="button" class="btn btn-outline-primary active" data-filter="all">Tutte</button>
                        <button type="button" class="btn btn-outline-primary" data-filter="active">Attive</button>
                        <button type="button" class="btn btn-outline-primary" data-filter="completed">Concluse</button>
                    </div>
                </div>

                <!-- Container per le prenotazioni attive -->
                <div id="activeBookingsContainer" class="mb-5">
                    <h3 class="mb-4">Prenotazioni Attive</h3>
                    <div class="row" id="activeBookings">
                        <!-- Le prenotazioni attive verranno inserite qui -->
                    </div>
                </div>

                <!-- Container per le prenotazioni concluse -->
                <div id="completedBookingsContainer" class="mb-5">
                    <h3 class="mb-4">Prenotazioni Concluse</h3>
                    <div class="row" id="completedBookings">
                        <!-- Le prenotazioni concluse verranno inserite qui -->
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<!-- Modal di conferma eliminazione -->
<div id="deleteModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Conferma Eliminazione</h5>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/profile-validation.js"></script>
<script src="${pageContext.request.contextPath}/js/mentee-dashboard.js"></script>
</body>
</html>
