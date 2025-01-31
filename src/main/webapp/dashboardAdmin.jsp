<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${sessionScope.ruolo != 'ADMIN'}">
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
    <title>Admin Dashboard - EasyCoach</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/dashboard.css" rel="stylesheet">
    <style>
        .user-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
        }
        .user-table th {
            background-color: #1a3b5d;
            color: white;
        }
        .sessions-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            padding: 20px;
        }
        .card {
            max-width: 600px;
            margin: 0 auto;
        }

        .card-img-top {
            height: 200px;
            object-fit: cover;
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
                        <a class="nav-link active" href="#" id="showUsers">
                            <i class="bi bi-people"></i> Visualizza Utenti
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" id="showSessions">
                            <i class="bi bi-calendar-event"></i> Visualizza Sessioni
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Contenuto principale -->
        <main class="col-md-10 ms-sm-auto px-md-4">
            <!-- Sezione Utenti -->
            <div id="usersSection">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Gestione Utenti</h1>
                </div>
                <!-- Filtri utenti -->
                <div class="row mb-3">
                    <div class="col-md-4">
                        <div class="input-group">
                            <input type="text" id="userSearchInput" class="form-control" placeholder="Cerca per nome o email...">
                            <button class="btn btn-outline-secondary" type="button" id="userSearchBtn">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select id="userRoleFilter" class="form-select">
                            <option value="">Tutti i ruoli</option>
                            <option value="ADMIN">Admin</option>
                            <option value="MENTOR">Mentor</option>
                            <option value="MENTEE">Mentee</option>
                        </select>
                    </div>
                </div>
                <div class="table-responsive user-table">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Ruolo</th>
                            <th>Azioni</th>
                        </tr>
                        </thead>
                        <tbody id="usersTableBody">
                        <!-- Gli utenti verranno inseriti qui dinamicamente -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Sezione Sessioni -->
            <div id="sessionsSection" style="display: none;">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1>Gestione Sessioni</h1>
                </div>
                <!-- Filtri sessioni -->
                <div class="row mb-3">
                    <div class="col-md-4">
                        <div class="input-group">
                            <input type="text" id="sessionSearchInput" class="form-control" placeholder="Cerca per titolo...">
                            <button class="btn btn-outline-secondary" type="button" id="sessionSearchBtn">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select id="sessionStatusFilter" class="form-select">
                            <option value="">Tutti gli stati</option>
                            <option value="ATTIVA">Attiva</option>
                            <option value="ARCHIVIATA">Archiviata</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <div class="input-group">
                            <span class="input-group-text">Prezzo max</span>
                            <input type="number" id="sessionPriceFilter" class="form-control" min="0" step="0.01">
                        </div>
                    </div>
                </div>
                <div class="sessions-grid" id="sessionsGrid">
                </div>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Definizione della variabile contextPath globale
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
</body>
</html>
