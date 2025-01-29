<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${sessionScope.ruolo != 'MENTOR'}">
    <c:redirect url="index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crea Sessione - EasyCoach</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="css/styles.css">
    <link rel="stylesheet" href="css/session.css">
</head>
<body class="bg-light">
<jsp:include page="WEB-INF/header.jsp" />

<div class="floating-shapes">
    <div class="shape shape-1"></div>
    <div class="shape shape-2"></div>
    <div class="shape shape-3"></div>
</div>

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 mt-5">
            <div class="card">
                <div class="card-header">
                    <h2 class="text-center mb-0">
                        <i class="bi bi-calendar-plus me-2"></i>
                        Crea una Nuova Sessione di Coaching
                    </h2>
                </div>
                <div class="card-body p-4">
                    <form id="sessionForm" class="fade-in" action="CreateSessionServlet" method="POST" enctype="multipart/form-data">
                        <input type="hidden" id="mentorId" value="${sessionScope.idUtente}">

                        <div class="form-floating mb-3">
                            <input type="text" class="form-control" id="titolo" name="titolo" placeholder="Titolo" required>
                            <label for="titolo">Titolo della Sessione</label>
                            <div id="titolo-error" class="text-danger mt-1 small"></div>
                        </div>

                        <div class="form-floating mb-3">
                            <textarea class="form-control" id="descrizione" name="descrizione" placeholder="Descrizione" style="height: 100px" required></textarea>
                            <label for="descrizione">Descrizione</label>
                            <div id="descrizione-error" class="text-danger mt-1 small"></div>
                        </div>

                        <div class="form-floating mb-4">
                            <input type="number" class="form-control" id="prezzo" name="prezzo" step="0.01" min="0" placeholder="Prezzo" required>
                            <label for="prezzo">Prezzo (€)</label>
                            <div id="prezzo-error" class="text-danger mt-1 small"></div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold mb-3">
                                <i class="bi bi-image me-2"></i>
                                Immagine della Sessione
                            </label>
                            <div class="image-preview-container mx-auto">
                                <input type="file" class="form-control" id="immagine" name="immagine" accept="image/*">
                                <div id="imagePreview" class="mt-2 d-none position-absolute">
                                    <img src="" alt="Preview" class="img-fluid">
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold mb-3">
                                <i class="bi bi-clock me-2"></i>
                                TimeSlot Disponibili
                            </label>
                            <div class="table-responsive table-timeslot">
                                <table class="table table-bordered mb-0" id="timeSlotTable">
                                    <thead>
                                    <tr>
                                        <th class="text-center">Orario</th>
                                        <th class="text-center">Lun</th>
                                        <th class="text-center">Mar</th>
                                        <th class="text-center">Mer</th>
                                        <th class="text-center">Gio</th>
                                        <th class="text-center">Ven</th>
                                        <th class="text-center">Sab</th>
                                        <th class="text-center">Dom</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <!-- Le righe verranno generate dinamicamente dal JavaScript -->
                                    </tbody>
                                </table>
                            </div>

                            <div class="selected-timeslots">
                                <h5 class="mb-3">
                                    <i class="bi bi-check-circle me-2"></i>
                                    TimeSlot Selezionati
                                </h5>
                                <ul class="list-group list-group-flush" id="selectedTimeslotsList">
                                </ul>
                            </div>
                        </div>

                        <div class="text-center mt-4">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-plus-circle me-2"></i>
                                Crea Sessione
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal di Successo -->
<div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="successModalLabel">
                    <i class="bi bi-check-circle me-2"></i>
                    Operazione Completata
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center p-4">
                <i class="bi bi-check-circle-fill text-success" style="font-size: 48px;"></i>
                <p class="mt-3 mb-0">La sessione è stata creata con successo!</p>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Quando il modal viene mostrato, imposta il timer per il reindirizzamento
    document.getElementById('successModal').addEventListener('shown.bs.modal', function () {
        setTimeout(function() {
            window.location.href = 'dashboardMentor.jsp';
        }, 2000);
    });

    // Preview dell'immagine
    document.getElementById('immagine').addEventListener('change', function(e) {
        const preview = document.getElementById('imagePreview');
        const file = e.target.files[0];
        const reader = new FileReader();

        reader.onload = function(e) {
            preview.querySelector('img').src = e.target.result;
            preview.classList.remove('d-none');
        }

        if (file) {
            reader.readAsDataURL(file);
        }
    });
</script>

<script src="js/session-validazione.js"></script>
</body>
</html>