<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyCoach - La tua piattaforma di mentoring</title>

    <link rel="stylesheet" href="css/style.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>

<jsp:include page="WEB-INF/header.jsp" />

<!-- Hero Section -->
<header class="hero" id="home">
    <div class="hero-content">
        <div class="hero-text">
            <h1>La tua crescita professionale inizia qui</h1>
            <p>Connettiti con mentor esperti, impara nuove competenze e raggiungi i tuoi obiettivi professionali</p>
            <div class="hero-buttons">
                <a href="register.jsp" class="cta-button primary">Inizia Ora</a>
                <a href="#how-it-works" class="cta-button secondary">Scopri di più</a>
            </div>
        </div>
        <div class="hero-image">
            <div class="shape-1"></div>
            <div class="shape-2"></div>
            <div class="shape-3"></div>
        </div>
    </div>
</header>

<!-- Come Funziona Section -->
<section class="how-it-works" id="how-it-works">
    <div class="section-header">
        <h2>Come Funziona</h2>
        <p>Tre semplici passi per iniziare il tuo percorso di crescita</p>
    </div>
    <div class="steps-container">
        <div class="step-card">
            <div class="step-icon">
                <i class="fas fa-search"></i>
            </div>
            <h3>1. Trova il tuo Mentor</h3>
            <p>Esplora i profili dei nostri mentor esperti e trova quello più adatto alle tue esigenze</p>
        </div>
        <div class="step-card">
            <div class="step-icon">
                <i class="fas fa-calendar-alt"></i>
            </div>
            <h3>2. Prenota una Sessione</h3>
            <p>Scegli l'orario che preferisci e prenota la tua sessione di mentoring personalizzata</p>
        </div>
        <div class="step-card">
            <div class="step-icon">
                <i class="fas fa-graduation-cap"></i>
            </div>
            <h3>3. Inizia ad Imparare</h3>
            <p>Partecipa alle sessioni e inizia il tuo percorso di crescita professionale</p>
        </div>
    </div>
</section>

<!-- Mentor Più Attivi -->
<section class="top-mentors">
    <div class="section-header">
        <h2>I Nostri Mentor Più Attivi</h2>
        <p>Scopri i mentor con più esperienza sulla nostra piattaforma</p>
    </div>
    <div class="mentors-grid">
        <c:forEach var="mentor" items="${mentorPiuAttivi}">
            <div class="mentor-card">
                <div class="mentor-header">
                    <div class="mentor-avatar">
                        <i class="fas fa-user-circle"></i>
                    </div>
                </div>
                <div class="mentor-info">
                    <h3>${mentor.nome} ${mentor.cognome}</h3>
                    <p class="mentor-email">${mentor.email}</p>
                </div>
                <div class="mentor-footer">
                    <a href="mentor?id=${mentor.idUtente}" class="btn btn-details">Visualizza Profilo</a>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<!-- Sessioni in Evidenza -->
<section class="featured-sessions">
    <div class="section-header">
        <h2>Sessioni in Evidenza</h2>
        <p>Scopri le sessioni più popolari della nostra piattaforma</p>
    </div>
    <div class="sessions-grid">
        <c:forEach var="sessione" items="${sessioniInEvidenza}">
            <div class="session-card">
                <div class="session-image">
                    <img src="${sessione.immagine}" alt="${sessione.titolo}">
                </div>
                <div class="session-content">
                    <h3>${sessione.titolo}</h3>
                    <p>${sessione.descrizione}</p>
                    <div class="session-footer">
                        <span class="price">€ ${String.format("%.2f", sessione.prezzo)}</span>
                        <a href="sessione?id=${sessione.idSessione}" class="btn btn-details">Scopri di più</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<!-- Statistiche Section -->
<section class="stats">
    <div class="stats-container">
        <div class="stat-item">
            <i class="fas fa-users"></i>
            <h3>1000+</h3>
            <p>Mentor Attivi</p>
        </div>
        <div class="stat-item">
            <i class="fas fa-graduation-cap"></i>
            <h3>5000+</h3>
            <p>Studenti Soddisfatti</p>
        </div>
        <div class="stat-item">
            <i class="fas fa-chalkboard-teacher"></i>
            <h3>10000+</h3>
            <p>Sessioni Completate</p>
        </div>
        <div class="stat-item">
            <i class="fas fa-star"></i>
            <h3>4.8/5</h3>
            <p>Rating Medio</p>
        </div>
    </div>
</section>

<!-- CTA Section -->
<section class="cta-section">
    <div class="cta-content">
        <h2>Pronto a iniziare il tuo percorso?</h2>
        <p>Unisciti a migliaia di professionisti che stanno già crescendo con EasyCoach</p>
        <a href="register.jsp" class="cta-button primary">Registrati Ora</a>
    </div>
</section>

<jsp:include page="WEB-INF/footer.jsp" />

<script>
    // Animazioni al scroll
    document.addEventListener('DOMContentLoaded', function() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate');
                }
            });
        }, {
            threshold: 0.1
        });

        document.querySelectorAll('.step-card, .session-card, .stat-item, .mentor-card').forEach((el) => observer.observe(el));
    });
</script>

</body>
</html>