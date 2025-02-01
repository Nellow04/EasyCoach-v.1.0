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
    <title>Pagamento - EasyCoach</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background-color: #f8f9fa;
        }
        .payment-container {
            width: 100%;
            max-width: 500px;
            padding: 30px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .payment-methods {
            display: flex;
            gap: 10px;
            margin-bottom: 25px;
            flex-wrap: wrap;
        }
        .payment-method {
            flex: 1;
            min-width: 120px;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            background: white;
            transition: all 0.3s ease;
        }
        .payment-method.active {
            border-color: #3f51b5;
            background-color: #f8f9ff;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .payment-method:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .payment-method img {
            height: 24px;
            width: auto;
        }
        .form-control {
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #ddd;
        }
        .btn-pay {
            width: 100%;
            padding: 15px;
            background-color: #3f51b5;
            border: none;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            margin-top: 20px;
            transition: background-color 0.3s ease;
        }
        .btn-pay:disabled {
            background-color: #3f51b5 !important;
            color: white !important;
            opacity: 0.5;
            cursor: not-allowed;
        }
        .btn-pay:hover {
            background-color: #32408f;
            color: white;
        }
        .payment-info {
            margin-top: 20px;
            padding: 15px;
            border-radius: 8px;
            background-color: #f8f9ff;
            border: 1px solid #e0e0e0;
        }
        #loadingOverlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255,255,255,0.9);
            z-index: 1000;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }
        .spinner-border {
            width: 3rem;
            height: 3rem;
        }
        .invalid-feedback {
            display: none;
            font-size: 80%;
            color: #dc3545;
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>
<div id="loadingOverlay">
    <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
    </div>
    <div class="mt-3" id="loadingText">Elaborazione del pagamento in corso...</div>
</div>

<div class="payment-container">
    <form id="paymentForm" class="needs-validation" novalidate>
        <input type="hidden" id="idPrenotazione" name="idPrenotazione" value="${param.idPrenotazione}">

        <div class="payment-methods">
            <div class="payment-method active" data-method="CARTA">
                <img src="https://cdn-icons-png.flaticon.com/512/633/633611.png" alt="Card">
                <span>Carta</span>
            </div>
            <div class="payment-method" data-method="PAYPAL">
                <img src="https://cdn-icons-png.flaticon.com/512/174/174861.png" alt="PayPal">
                <span>PayPal</span>
            </div>
            <div class="payment-method" data-method="GOOGLEPAY">
                <img src="https://cdn-icons-png.flaticon.com/512/300/300221.png" alt="Google Pay">
                <span>Google Pay</span>
            </div>
        </div>

        <div id="cartaFields">
            <h5 class="mb-4">Dettagli Carta</h5>

            <div class="form-group">
                <label for="numeroCarta">Numero Carta</label>
                <input type="text" class="form-control" id="numeroCarta" name="numeroCarta"
                       placeholder="1111 1111 1111 1111" pattern="[0-9\s]{19}" maxlength="19" required>
                <div class="invalid-feedback">
                    Inserisci un numero di carta valido nel formato 1111 1111 1111 1111
                </div>
            </div>

            <div class="form-row">
                <div class="form-group col-8">
                    <label for="scadenzaGGMM">Data di Scadenza (GG/MM)</label>
                    <input type="text" class="form-control" id="scadenzaGGMM" name="scadenzaGGMM"
                           placeholder="GG/MM" pattern="(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])" maxlength="5" required>
                    <div class="invalid-feedback">
                        Inserisci una data valida nel formato GG/MM
                    </div>
                </div>
                <div class="form-group col-4">
                    <label for="scadenzaAnno">Anno</label>
                    <input type="text" class="form-control" id="scadenzaAnno" name="scadenzaAnno"
                           placeholder="YYYY" pattern="[0-9]{4}" maxlength="4" required>
                    <div class="invalid-feedback">
                        Anno non valido
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label for="cardHolder">Titolare della Carta</label>
                <input type="text" class="form-control" id="cardHolder" name="cardHolder"
                       placeholder="Nome e Cognome" pattern="^[A-Za-zÀ-ÿ\s']{2,}$" required>
                <div class="invalid-feedback">
                    Inserisci un nome valido (minimo 2 caratteri, solo lettere, spazi e apostrofi)
                </div>
            </div>

            <div class="form-group">
                <label for="cvv">CVV</label>
                <div class="input-group">
                    <input type="password" class="form-control" id="cvv" name="cvv"
                           placeholder="123" pattern="[0-9]{3,4}" maxlength="4" required
                           autocomplete="off">
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary" type="button" id="toggleCvv">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                    <div class="invalid-feedback">
                        CVV non valido (3-4 cifre)
                    </div>
                </div>
            </div>
        </div>

        <div id="paypalFields" style="display: none;">
            <div class="payment-info">
                <p class="mb-2">Verrai reindirizzato a PayPal per completare il pagamento in modo sicuro.</p>
                <small class="text-muted">Nota: Per questa demo, il reindirizzamento sarà simulato.</small>
            </div>
        </div>

        <div id="googlepayFields" style="display: none;">
            <div class="payment-info">
                <p class="mb-2">Completa il pagamento con Google Pay in modo rapido e sicuro.</p>
                <small class="text-muted">Nota: Per questa demo, il pagamento con Google Pay sarà simulato.</small>
            </div>
        </div>

        <input type="hidden" id="totalePagato" name="totalePagato" value="${param.totale}">
        <button type="submit" class="btn btn-pay">Paga €${param.totale}</button>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/payment.js"></script>
</body>
</html>