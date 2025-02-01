<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.idUtente}">
    <c:redirect url="index.jsp"/>  <!-- Reindirizza alla pagina di login -->
</c:if>

<c:if test="${sessionScope.ruolo == 'MENTOR'}">
    <c:redirect url="index.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <title>Pagamento Completato - EasyCoach</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .success-container {
            max-width: 500px;
            margin: 100px auto;
            text-align: center;
            padding: 40px 20px;
        }
        .success-icon {
            color: #28a745;
            font-size: 64px;
            margin-bottom: 20px;
        }
        .btn-home {
            margin-top: 30px;
        }
    </style>
</head>
<body class="bg-light">
    <div class="success-container">
        <div class="success-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" class="bi bi-check-circle-fill" viewBox="0 0 16 16">
                <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
            </svg>
        </div>
        <h2>Pagamento Completato con Successo!</h2>
        <p class="text-muted mt-3">Grazie per il tuo acquisto. Riceverai una email di conferma con i dettagli della transazione.</p>
        <a href="index.jsp" class="btn btn-primary btn-home">Torna alla Home</a>
    </div>
</body>
</html>
