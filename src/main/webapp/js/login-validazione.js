// Riutilizziamo le funzioni di validazione base dal file esistente
function validateEmail(email) {
    if (!email) return { isValid: false, message: "Email richiesta" };

    const localPart = email.split('@')[0];
    if (localPart.length > 25) {
        return {
            isValid: false,
            message: "La parte prima della @ non può superare i 25 caratteri"
        };
    }

    const emailRegex = /^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailRegex.test(email)) {
        return {
            isValid: false,
            message: "Formato email non valido"
        };
    }

    return { isValid: true, message: "" };
}

async function hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hash = await crypto.subtle.digest('SHA-256', data);
    return Array.from(new Uint8Array(hash))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
}

function createErrorSpan(input) {
    let span = input.nextElementSibling;
    if (!span || !span.classList.contains('error-message')) {
        span = document.createElement('span');
        span.className = 'error-message';
        span.style.color = '#dc3545';
        span.style.fontSize = '0.8em';
        span.style.display = 'none';
        input.parentNode.insertBefore(span, input.nextSibling);
    }
    return span;
}

function showError(element, message) {
    element.textContent = message;
    element.style.display = 'block';
}

function clearError(element) {
    element.textContent = '';
    element.style.display = 'none';
}

// Funzione principale per il setup della validazione del login
function setupLoginValidation() {
    const form = document.querySelector('form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const submitButton = form.querySelector('button[type="submit"]');

    if (!form || !emailInput || !passwordInput || !submitButton) return;

    // Controlla se ci sono errori dalla servlet
    const servletError = document.querySelector('input[name="errore"]');
    if (servletError && servletError.value) {
        const errorSpan = createErrorSpan(emailInput);
        showError(errorSpan, servletError.value);
        emailInput.classList.add('invalid');
        passwordInput.classList.add('invalid');
    }

    // Setup validazione email
    const emailErrorSpan = createErrorSpan(emailInput);
    emailInput.addEventListener('input', function() {
        const result = validateEmail(this.value);
        if (result.isValid) {
            clearError(emailErrorSpan);
            emailInput.classList.remove('invalid');
            emailInput.classList.add('valid');
        } else {
            showError(emailErrorSpan, result.message);
            emailInput.classList.remove('valid');
            emailInput.classList.add('invalid');
        }
        updateSubmitButton();
    });

    // Setup validazione password (verifica solo che non sia vuota)
    const passwordErrorSpan = createErrorSpan(passwordInput);
    passwordInput.addEventListener('input', function() {
        if (this.value.trim()) {
            clearError(passwordErrorSpan);
            passwordInput.classList.remove('invalid');
            passwordInput.classList.add('valid');
        } else {
            showError(passwordErrorSpan, "Password richiesta");
            passwordInput.classList.remove('valid');
            passwordInput.classList.add('invalid');
        }
        updateSubmitButton();
    });

    // Funzione per aggiornare lo stato del pulsante submit
    function updateSubmitButton() {
        const isValid = !emailInput.classList.contains('invalid') &&
            !passwordInput.classList.contains('invalid') &&
            emailInput.value.trim() &&
            passwordInput.value.trim();
        submitButton.disabled = !isValid;
    }

    // Gestione submit del form
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        try {
            const hashedPassword = await hashPassword(passwordInput.value);
            passwordInput.value = hashedPassword; // Impostiamo la password hashata nel campo password originale
            form.submit();
        } catch (error) {
            console.error('Errore durante la crittografia:', error);
            showError(passwordErrorSpan, "Errore durante il login");
        }
    });
}

// Aggiungiamo gli stili CSS
const style = document.createElement('style');
style.textContent = `
    .error-message {
        color: #dc3545;
        margin-top: 5px;
        display: none;
    }
    input.valid {
        border-color: #28a745;
    }
    input.invalid {
        border-color: #dc3545;
    }
`;
document.head.appendChild(style);

// Esportiamo le funzioni
window.loginUtils = {
    setupLoginValidation
};

// Inizializziamo la validazione quando il DOM è caricato
document.addEventListener('DOMContentLoaded', setupLoginValidation);