// Funzioni di validazione
function validatePassword(password) {
    if (!password) return { isValid: false, message: "Password richiesta" };

    const checks = {
        length: password.length >= 8,
        letter: /[A-Za-z]/.test(password),
        number: /\d/.test(password)
    };

    if (!checks.length) {
        return {
            isValid: false,
            message: "La password deve contenere almeno 8 caratteri"
        };
    }
    if (!checks.letter || !checks.number) {
        return {
            isValid: false,
            message: "La password deve contenere almeno una lettera e un numero"
        };
    }

    return { isValid: true, message: "" };
}

async function hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

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

function setupPasswordValidation(input) {
    const errorSpan = createErrorSpan(input);
    let timer;

    input.addEventListener('input', function() {
        clearTimeout(timer);
        timer = setTimeout(() => {
            const result = validatePassword(this.value);
            if (result.isValid) {
                clearError(errorSpan);
                input.classList.remove('invalid');
                input.classList.add('valid');
            } else {
                showError(errorSpan, result.message);
                input.classList.remove('valid');
                input.classList.add('invalid');
            }
            updateSubmitButton();
        }, 300);
    });
}

function setupConfirmPasswordValidation(confirmInput, originalInput) {
    const errorSpan = createErrorSpan(confirmInput);
    let timer;

    confirmInput.addEventListener('input', function() {
        clearTimeout(timer);
        timer = setTimeout(() => {
            if (!this.value) {
                showError(errorSpan, "Conferma password richiesta");
                confirmInput.classList.remove('valid');
                confirmInput.classList.add('invalid');
            } else if (this.value !== originalInput.value) {
                showError(errorSpan, "Le password non coincidono");
                confirmInput.classList.remove('valid');
                confirmInput.classList.add('invalid');
            } else {
                clearError(errorSpan);
                confirmInput.classList.remove('invalid');
                confirmInput.classList.add('valid');
            }
            updateSubmitButton();
        }, 300);
    });
}

function updateSubmitButton() {
    const form = document.getElementById('profileForm');
    if (!form) return;

    const submitButton = form.querySelector('button[type="submit"]');
    if (!submitButton) return;

    const inputs = form.querySelectorAll('input[required]');
    const isValid = Array.from(inputs).every(input => !input.classList.contains('invalid'));

    submitButton.disabled = !isValid;
}

async function validatePasswordForm(event) {
    event.preventDefault();

    const form = document.getElementById('profileForm');
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    const currentPassword = currentPasswordInput.value;
    const newPassword = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    // Verifica che tutti i campi siano compilati
    if (!currentPassword || !newPassword || !confirmPassword) {
        alert('Tutti i campi sono obbligatori');
        return false;
    }

    // Verifica che la nuova password sia diversa dalla password attuale
    if (currentPassword === newPassword) {
        alert('La nuova password deve essere diversa dalla password attuale');
        return false;
    }

    // Verifica che la nuova password e la conferma coincidano
    if (newPassword !== confirmPassword) {
        alert('Le password non coincidono');
        return false;
    }

    // Verifica che la nuova password rispetti i requisiti minimi
    const passwordValidation = validatePassword(newPassword);
    if (!passwordValidation.isValid) {
        alert(passwordValidation.message);
        return false;
    }

    try {
        // Hash delle password
        const hashedCurrentPassword = await hashPassword(currentPassword);
        const hashedNewPassword = await hashPassword(newPassword);

        // Imposta i valori hashati direttamente nei campi
        currentPasswordInput.value = hashedCurrentPassword;
        newPasswordInput.value = hashedNewPassword;
        confirmPasswordInput.value = hashedNewPassword;

        // Invia il form
        form.submit();
    } catch (error) {
        console.error('Errore durante l\'hashing delle password:', error);
        alert('Errore durante la validazione delle password');
        return false;
    }
}

function confirmDelete() {
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    deleteModal.show();
}

function closeModal() {
    const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
    if (deleteModal) {
        deleteModal.hide();
    }
}

function deleteAccount() {
    fetch('RemoveAccountServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ action: 'deleteAccount' }),
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.success) {
                alert('Account eliminato con successo.');
                window.location.href = 'index.jsp';
            } else {
                alert('Errore nell\'eliminazione dell\'account: ' + data.error);
            }
        })
        .catch((error) => {
            alert('Errore del server: ' + error.message);
        })
        .finally(() => {
            closeModal();
        });
}

// Setup della validazione quando il documento è caricato
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('profileForm');
    if (form) {
        form.addEventListener("submit", validatePasswordForm);
    }

    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    if (currentPasswordInput) {
        setupPasswordValidation(currentPasswordInput);
    }

    if (newPasswordInput) {
        setupPasswordValidation(newPasswordInput);
    }

    if (confirmPasswordInput && newPasswordInput) {
        setupConfirmPasswordValidation(confirmPasswordInput, newPasswordInput);
    }

    // Aggiungi gli stili CSS
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
});