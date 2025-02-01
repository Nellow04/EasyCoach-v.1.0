<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dettagli Sessione - EasyCoach</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/session-details.css">
</head>
<body>
<jsp:include page="WEB-INF/header.jsp" />

<div class="main-content">
    <!-- Forme animate -->
    <div class="shape-1"></div>
    <div class="shape-2"></div>
    <div class="shape-3"></div>

    <div class="container">
        <div class="session-container">
            <!-- Immagine Sessione -->
            <div class="session-image-container">
                <img src="${sessione.immagine}" class="session-image" alt="${sessione.titolo}">
            </div>

            <!-- Dettagli e Form Prenotazione -->
            <div class="session-details">
                <h1 class="session-title">${sessione.titolo}</h1>
                <p class="session-description">${sessione.descrizione}</p>
                <div class="session-price">€${sessione.prezzo}</div>

                <!-- Form Prenotazione -->
                <div class="booking-section">
                    <c:choose>
                        <c:when test="${sessionScope.ruolo eq 'MENTOR'}">
                            <div class="alert alert-info">
                                I mentor non possono effettuare prenotazioni
                            </div>
                        </c:when>
                        <c:when test="${empty sessionScope.ruolo}">
                            <div class="alert alert-warning">
                                Effettua il login per prenotare questa sessione
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form id="bookingForm" action="BookingServlet" method="POST">
                                <input type="hidden" name="idUtente" id="idUtente" value="${sessionScope.idUtente}">
                                <input type="hidden" name="selectedTimeslot" id="selectedTimeslot">
                                <input type="hidden" name="selectedDate" id="selectedDate">

                                <div class="mb-4">
                                    <label for="datePicker" class="form-label">Seleziona una data</label>
                                    <input type="text" class="form-control" id="datePicker" placeholder="Seleziona una data" required>
                                </div>

                                <div id="timeslotsContainer" class="mb-4 d-none">
                                    <label class="form-label">Orari disponibili:</label>
                                    <div id="timeslots" class="d-flex flex-wrap">
                                        <!-- I timeslot verranno inseriti qui dinamicamente -->
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary w-100" id="bookButton" disabled>
                                    Prenota Sessione
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Sessioni Correlate -->
        <section class="py-5">
            <div class="container">
                <h2 class="text-center mb-5">Sessioni Correlate</h2>
                <div class="row g-4">
                    <c:forEach items="${sessioniCorrelate}" var="sessione">
                        <div class="col-md-3">
                            <div class="card featured-card h-100">
                                <img src="${sessione.immagine}" class="card-img-top p-3" alt="${sessione.titolo}">
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">${sessione.titolo}</h5>
                                    <p class="card-text text-muted">
                                        <c:set var="desc" value="${sessione.descrizione}"/>
                                        <c:if test="${fn:length(desc) > 100}">
                                            <c:set var="desc" value="${fn:substring(desc, 0, 97)}..."/>
                                        </c:if>
                                            ${desc}
                                    </p>
                                    <div class="mt-auto d-flex justify-content-between align-items-center">
                                        <span class="h5 mb-0">€${sessione.prezzo}</span>
                                        <a href="GetSessionServlet?id=${sessione.idSessione}" class="btn btn-outline-primary">Dettagli</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/it.js"></script>
<script src="js/session-details.js"></script>
</body>
</html>