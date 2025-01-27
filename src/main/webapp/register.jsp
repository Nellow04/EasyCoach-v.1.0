<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MentorApp - Registrazione</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <script src="${pageContext.request.contextPath}/js/register-validazione.js"></script>
</head>
<body>
<div class="login-container">
    <a href="${pageContext.request.contextPath}/index.jsp" class="back-button">
        <i class="fas fa-arrow-left"></i>
    </a>
    <div class="login-box">
        <h1>Registrazione</h1>
        <form id="registrationForm" action="${pageContext.request.contextPath}/RegisterServlet" method="post">
            <div class="form-group">
                <div class="input-with-icon">
                    <select id="ruolo" name="ruolo" required>
                        <option value="">Seleziona un ruolo</option>
                        <option value="MENTOR">Mentor</option>
                        <option value="MENTEE">Mentee</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-envelope"></i>
                    <input type="email" id="email" name="email" placeholder="Email" required>
                </div>
            </div>
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-user"></i>
                    <input type="text" id="nome" name="nome" placeholder="Nome" required>
                </div>
            </div>
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-user"></i>
                    <input type="text" id="cognome" name="cognome" placeholder="Cognome" required>
                </div>
            </div>
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-lock"></i>
                    <input type="password" id="password" name="password" placeholder="Password" required>
                </div>
            </div>
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-lock"></i>
                    <input type="password" id="confermaPassword" name="confermaPassword" placeholder="Conferma Password" required>
                </div>
            </div>
            <button type="submit" class="login-button">Registrati</button>
            <div class="login-footer">
                <p>Hai già un account? <a href="${pageContext.request.contextPath}/login.jsp" class="register-link">Accedi</a></p>
            </div>
        </form>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        validationUtils.setupValidation('registrationForm');
    });
</script>
</body>
</html>