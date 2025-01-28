// Funzioni di validazione
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

function validateName(name) {
    if (!name) return { isValid: false, message: "Campo richiesto" };

    if (name.length > 25) {
        return {
            isValid: false,
            message: "Il campo non può superare i 25 caratteri"
        };
    }

    const nameRegex = /^[A-Za-zÀ-ÿ\s']{2,}$/;
    if (!nameRegex.test(name)) {
        return {
            isValid: false,
            message: "Inserire un nome valido (solo lettere)"
        };
    }

    return { isValid: true, message: "" };
}

function validatePassword(password) {
    if (!password) return { isValid: false, message: "Password richiesta" };

    const checks = {
        length: password.length >= 8,
        uppercase: /[A-Z]/.test(password),
        number: /\d/.test(password)
    };

    if (!checks.length) {
        return {
            isValid: false,
            message: "Minimo 8 caratteri"
        };
    }
    if (!checks.uppercase) {
        return {
            isValid: false,
            message: "Almeno una lettera maiuscola"
        };
    }
    if (!checks.number) {
        return {
            isValid: false,
            message: "Almeno un numero"
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

// Funzione per gestire la validazione in tempo reale
function setupRealTimeValidation(input, validationFn, debounceTime = 300) {
    let timer;
    const errorSpan = createErrorSpan(input);
    let isValid = false;

    input.addEventListener('input', function() {
        clearTimeout(timer);
        timer = setTimeout(async () => {
            const result = validationFn(this.value);

            if (result.isValid) {
                clearError(errorSpan);
                input.classList.remove('invalid');
                input.classList.add('valid');
                isValid = true;
            } else {
                showError(errorSpan, result.message);
                input.classList.remove('valid');
                input.classList.add('invalid');
                isValid = false;
            }

            updateSubmitButton();
        }, debounceTime);
    });

    return () => isValid;
}

// Funzione per verificare l'email con il server
async function checkEmailExists(email) {
    try {
        const response = await fetch('CheckEmailServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'email=' + encodeURIComponent(email)
        });
        const data = await response.json();
        return {
            exists: data.exists,
            message: data.exists ? "Email già registrata" : ""
        };
    } catch (error) {
        console.error('Errore durante il controllo email:', error);
        return {
            exists: false,
            message: "Errore durante la verifica dell'email"
        };
    }
}

// Funzione principale di setup
function setupValidation(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    const emailInput = form.querySelector('input[type="email"]');
    const nomeInput = form.querySelector('input[name="nome"]');
    const cognomeInput = form.querySelector('input[name="cognome"]');
    const passwordInput = form.querySelector('input[name="password"]');
    const confermaPasswordInput = form.querySelector('input[name="confermaPassword"]');
    const ruoloInput = form.querySelector('select[name="ruolo"]');
    const submitButton = form.querySelector('button[type="submit"]');

    // Funzione per controllare se tutti i campi sono validi
    function updateSubmitButton() {
        const allInputsFilled = [
            emailInput.value,
            nomeInput.value,
            cognomeInput.value,
            passwordInput.value,
            confermaPasswordInput.value,
            ruoloInput.value
        ].every(value => value.trim() !== '');

        const noErrors = !form.querySelectorAll('.error-message:not(:empty)').length &&
            !form.querySelectorAll('.invalid').length;

        submitButton.disabled = !(allInputsFilled && noErrors);
    }

    // Setup validazione email con controllo server
    if (emailInput) {
        let emailTimer;
        const emailErrorSpan = createErrorSpan(emailInput);

        emailInput.addEventListener('input', function() {
            clearTimeout(emailTimer);
            emailTimer = setTimeout(async () => {
                const basicValidation = validateEmail(this.value);
                if (!basicValidation.isValid) {
                    showError(emailErrorSpan, basicValidation.message);
                    emailInput.classList.add('invalid');
                    emailInput.classList.remove('valid');
                    updateSubmitButton();
                    return;
                }

                const serverCheck = await checkEmailExists(this.value);
                if (serverCheck.exists) {
                    showError(emailErrorSpan, serverCheck.message);
                    emailInput.classList.add('invalid');
                    emailInput.classList.remove('valid');
                } else {
                    clearError(emailErrorSpan);
                    emailInput.classList.remove('invalid');
                    emailInput.classList.add('valid');
                }
                updateSubmitButton();
            }, 500);
        });
    }

    // Setup altre validazioni
    const validators = [];
    if (nomeInput) {
        validators.push(setupRealTimeValidation(nomeInput, validateName));
    }
    if (cognomeInput) {
        validators.push(setupRealTimeValidation(cognomeInput, validateName));
    }
    if (passwordInput) {
        validators.push(setupRealTimeValidation(passwordInput, validatePassword));
    }
    if (confermaPasswordInput && passwordInput) {
        validators.push(setupRealTimeValidation(confermaPasswordInput, (value) => {
            return {
                isValid: value === passwordInput.value,
                message: value === passwordInput.value ? "" : "Le password non coincidono"
            };
        }));
    }

    // Aggiungi event listener per il campo ruolo
    if (ruoloInput) {
        ruoloInput.addEventListener('change', updateSubmitButton);
        ruoloInput.addEventListener('blur', updateSubmitButton);
    }

    // Aggiungi event listener per tutti gli input
    form.querySelectorAll('input, select').forEach(input => {
        input.addEventListener('input', updateSubmitButton);
        input.addEventListener('change', updateSubmitButton);
        input.addEventListener('blur', updateSubmitButton);
        // Trigger validation on load if field has a value
        if (input.value.trim() !== '') {
            const event = new Event('input', {
                bubbles: true,
                cancelable: true,
            });
            input.dispatchEvent(event);
        }
    });

    // Controllo iniziale del pulsante
    updateSubmitButton();

    // Gestione submit del form
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        // Verifica finale di tutti i campi
        const allValid = validators.every(validate => validate());

        if (allValid) {
            const hashedPassword = await hashPassword(passwordInput.value);
            const hashedConfirmPassword = await hashPassword(confermaPasswordInput.value);

            // Crea un nuovo input nascosto per la password hashata
            const hiddenHashedPassword = document.createElement('input');
            hiddenHashedPassword.type = 'hidden';
            hiddenHashedPassword.name = 'hashedPassword';
            hiddenHashedPassword.value = hashedPassword;

            // Crea un nuovo input nascosto per la conferma password hashata
            const hiddenHashedConfirmPassword = document.createElement('input');
            hiddenHashedConfirmPassword.type = 'hidden';
            hiddenHashedConfirmPassword.name = 'hashedConfirmPassword';
            hiddenHashedConfirmPassword.value = hashedConfirmPassword;

            // Aggiungi gli input nascosti al form
            form.appendChild(hiddenHashedPassword);
            form.appendChild(hiddenHashedConfirmPassword);

            // Rimuovi le password in chiaro
            passwordInput.removeAttribute('name');
            confermaPasswordInput.removeAttribute('name');

            // Invia il form
            form.submit();
        }
    });
}

// Funzioni di utilità
function createErrorSpan(input) {
    let span = input.nextElementSibling;
    if (!span || !span.classList.contains('error-message')) {
        span = document.createElement('span');
        span.className = 'error-message';
        span.style.color = 'red';
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

// Stili CSS per gli input
const style = document.createElement('style');
style.textContent = `
    input.valid {
        border-color: green !important;
        background-color: #f0fff0 !important;
    }
    
    input.invalid {
        border-color: red !important;
        background-color: #fff0f0 !important;
    }
    
    .error-message {
        margin-top: 4px;
        transition: all 0.3s ease;
    }
`;
document.head.appendChild(style);

// Esporta le funzioni per uso esterno
window.validationUtils = {
    validateEmail,
    validateName,
    validatePassword,
    checkEmailExists,
    setupValidation
}; 