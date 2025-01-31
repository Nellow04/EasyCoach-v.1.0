<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="css/header.css">
<link rel="stylesheet" href="css/notifications.css">

<header class="header" 
    data-user-type="${sessionScope.ruolo}"
    data-user-id="${sessionScope.idUtente}">
    <a href="${pageContext.request.contextPath}/home" class="logo">
        <img src="${pageContext.request.contextPath}/includes/logo.jpg" alt="EasyCoach Logo">
    </a>

    <button class="menu-toggle">
        <span></span>
        <span></span>
        <span></span>
    </button>

    <!-- Area Notifiche -->
    <div class="notifications-area">
        <c:if test="${sessionScope.ruolo eq 'MENTEE'}">
            <div id="menteeNotifications" class="notification-container"></div>
        </c:if>
        <c:if test="${sessionScope.ruolo eq 'MENTOR'}">
            <div id="mentorNotifications" class="notification-container"></div>
        </c:if>
    </div>

    <div class="nav-links">
        <c:choose>
            <c:when test="${empty sessionScope.ruolo}">
                <a href="${pageContext.request.contextPath}/about">Chi Siamo</a>
                <a href="${pageContext.request.contextPath}/login.jsp">Accedi</a>
                <a href="${pageContext.request.contextPath}/register.jsp" class="btn-join">Registrati</a>
            </c:when>
            
            <c:when test="${sessionScope.ruolo eq 'MENTOR'}">
                <a href="${pageContext.request.contextPath}/search.jsp">Esplora</a>
                <a href="${pageContext.request.contextPath}/session.jsp">Crea Sessione</a>
                <a href="${pageContext.request.contextPath}/dashboardMentor.jsp" class="profile-link">
                    Profilo
                    <span class="notification-badge" style="display: none">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/LogoutServlet">Esci</a>
            </c:when>

            <c:when test="${sessionScope.ruolo eq 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/dashboardAdmin.jsp.jsp" class="profile-link">
                    Dashboard Admin
                    <span class="notification-badge" style="display: none">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/LogoutServlet">Esci</a>
            </c:when>
            
            <c:when test="${sessionScope.ruolo eq 'MENTEE'}">
                <a href="${pageContext.request.contextPath}/search.jsp">Cerca Sessione</a>
                <a href="${pageContext.request.contextPath}/dashboardMentee.jsp" class="profile-link">
                    Profilo
                    <span class="notification-badge" style="display: none">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/LogoutServlet">Esci</a>
            </c:when>
        </c:choose>
    </div>
</header>

<script src="${pageContext.request.contextPath}/js/notifications.js"></script>

<script>
document.querySelector('.menu-toggle').addEventListener('click', function() {
    document.querySelector('.nav-links').classList.toggle('active');
    this.classList.toggle('active');
});
</script>
