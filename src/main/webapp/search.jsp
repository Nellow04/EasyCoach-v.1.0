<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.idUtente}">
    <c:redirect url="index.jsp"/>  <!-- Reindirizza alla pagina di login -->
</c:if>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyCoach - Ricerca Sessioni</title>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/search.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body class="search-page">

<jsp:include page="WEB-INF/header.jsp" />

<!-- Hero Section -->
<div class="search-hero">
    <div class="hero-content">
        <div class="hero-text">
            <h1>Cerca una sessione che fa per te</h1>
            <div class="search-container">
                <div class="search-wrapper">
                    <div class="search-input-wrapper">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" id="searchInput" class="search-bar" placeholder="Cerca una sessione per titolo...">
                    </div>
                    <div class="filter-wrapper">
                        <i class="fas fa-sort-amount-down filter-icon"></i>
                        <select id="sortSelect" class="filter-select">
                            <option value="relevance" selected>Rilevanza</option>
                            <option value="priceAsc">Prezzo Crescente</option>
                            <option value="priceDesc">Prezzo Decrescente</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <div class="hero-image">
            <div class="shape-1"></div>
            <div class="shape-2"></div>
            <div class="shape-3"></div>
        </div>
    </div>
</div>

<!-- Results Section -->
<section class="results-section">
    <div id="searchResults" class="ec-grid sessions-grid">
        <!-- AJAX-injected sessioni di coaching vanno qui -->
    </div>
</section>

<script src="<%=request.getContextPath()%>/js/search.js"></script>
</body>
</html>