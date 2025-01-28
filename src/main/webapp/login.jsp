<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MentorApp - Accedi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <script src="${pageContext.request.contextPath}/js/login-validazione.js"></script>
</head>
<body>
<div class="login-container">
    <a href="${pageContext.request.contextPath}/index.jsp" class="back-button">
        <i class="fas fa-arrow-left"></i>
    </a>
    <div class="login-box">
        <h1>Accedi</h1>
        <input type="hidden" name="errore" value="<%= request.getAttribute("errore") != null ? request.getAttribute("errore") : "" %>">
        <form action="${pageContext.request.contextPath}/LoginServlet" method="post">
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-envelope"></i>
                    <input type="email" id="email" name="email" placeholder="Email" required>
                </div>
            </div>
            <div class="form-group">
                <div class="input-with-icon">
                    <i class="fas fa-lock"></i>
                    <input type="password" id="password" name="password" placeholder="Password" required>
                </div>
            </div>
            <button type="submit" class="login-button">Accedi</button>
            <div class="login-footer">
                <p>Non hai un account? <a href="${pageContext.request.contextPath}/register.jsp" class="register-link">Registrati</a></p>
            </div>
        </form>
    </div>
</div>
</body>
</html>