document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('paymentForm');
    const paymentMethods = document.querySelectorAll('.payment-method');
    const cartaFields = document.getElementById('cartaFields');
    const paypalFields = document.getElementById('paypalFields');
    const googlepayFields = document.getElementById('googlepayFields');
    const loadingOverlay = document.getElementById('loadingOverlay');
    const loadingText = document.getElementById('loadingText');
    const submitButton = form.querySelector('button[type="submit"]');

    function updateSubmitButton() {
        const activeMethod = document.querySelector('.payment-method.active');
        if (activeMethod.getAttribute('data-method') === 'CARTA') {
            const hasErrors = Array.from(form.querySelectorAll('input[required]')).some(input => !input.validity.valid);
            submitButton.disabled = hasErrors;
        } else {
            submitButton.disabled = false;
        }
    }

    // Aggiungi l'evento input a tutti i campi required
    form.querySelectorAll('input[required]').forEach(input => {
        input.addEventListener('input', updateSubmitButton);
    });

    // Gestione dei metodi di pagamento
    paymentMethods.forEach(method => {
        method.addEventListener('click', function() {
            paymentMethods.forEach(m => m.classList.remove('active'));
            this.classList.add('active');

            const selectedMethod = this.getAttribute('data-method');
            cartaFields.style.display = 'none';
            paypalFields.style.display = 'none';
            googlepayFields.style.display = 'none';

            switch(selectedMethod) {
                case 'CARTA':
                    cartaFields.style.display = 'block';
                    enableCartaValidation(true);
                    break;
                case 'PAYPAL':
                    paypalFields.style.display = 'block';
                    enableCartaValidation(false);
                    break;
                case 'GOOGLEPAY':
                    googlepayFields.style.display = 'block';
                    enableCartaValidation(false);
                    break;
            }
            updateSubmitButton();
        });
    });

    // Formattazione e validazione del numero della carta
    const numeroCarta = document.getElementById('numeroCarta');
    numeroCarta.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        let formattedValue = '';
        for (let i = 0; i < value.length; i++) {
            if (i > 0 && i % 4 === 0) {
                formattedValue += ' ';
            }
            formattedValue += value[i];
        }
        e.target.value = formattedValue;

        // Validazione del formato
        const isValid = /^(\d{4}\s){3}\d{4}$/.test(formattedValue);
        this.setCustomValidity(isValid ? '' : 'Numero carta non valido');
        showValidationMessage(this);
    });

    // Validazione della data di scadenza
    const scadenzaGGMM = document.getElementById('scadenzaGGMM');
    const scadenzaAnno = document.getElementById('scadenzaAnno');

    function validateExpiryDate() {
        const [giorno, mese] = (scadenzaGGMM.value || '/').split('/').map(n => parseInt(n));
        const anno = parseInt(scadenzaAnno.value);

        if (!giorno || !mese || !anno) return;

        const oggi = new Date();
        const dataScadenza = new Date(anno, mese - 1, giorno);

        // Verifica se la data è valida
        if (dataScadenza.getDate() !== giorno ||
            dataScadenza.getMonth() + 1 !== mese ||
            dataScadenza.getFullYear() !== anno) {
            scadenzaGGMM.setCustomValidity('Data non valida');
            showValidationMessage(scadenzaGGMM);
            return false;
        }

        // Verifica se la carta è scaduta
        if (dataScadenza < oggi) {
            scadenzaAnno.setCustomValidity('Carta scaduta');
            showValidationMessage(scadenzaAnno);
            return false;
        }

        scadenzaGGMM.setCustomValidity('');
        scadenzaAnno.setCustomValidity('');
        showValidationMessage(scadenzaGGMM);
        showValidationMessage(scadenzaAnno);
        return true;
    }

    // Formattazione automatica della data GG/MM
    scadenzaGGMM.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 0) {
            if (value.length > 2) {
                value = value.slice(0, 2) + '/' + value.slice(2, 4);
            }
            if (value.length === 2) {
                value += '/';
            }
        }
        e.target.value = value;
        validateExpiryDate();
    });

    scadenzaAnno.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 4) value = value.slice(0, 4);
        e.target.value = value;
        validateExpiryDate();
    });

    // Validazione del titolare della carta
    const cardHolder = document.getElementById('cardHolder');
    cardHolder.addEventListener('input', function(e) {
        const isValid = /^[A-Za-zÀ-ÿ\s']{2,}$/.test(e.target.value);
        this.setCustomValidity(isValid ? '' : 'Nome non valido');
        showValidationMessage(this);
    });

    // Gestione del campo CVV
    const cvvInput = document.getElementById('cvv');
    const toggleCvvButton = document.getElementById('toggleCvv');
    const toggleCvvIcon = toggleCvvButton.querySelector('i');

    // Permetti solo numeri nel campo CVV
    cvvInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 4) value = value.slice(0, 4);
        e.target.value = value;
    });

    // Toggle visibilità CVV
    toggleCvvButton.addEventListener('mousedown', function(e) {
        e.preventDefault(); // Previene il focus sul bottone
        cvvInput.type = 'text';
        toggleCvvIcon.classList.remove('fa-eye');
        toggleCvvIcon.classList.add('fa-eye-slash');
    });

    toggleCvvButton.addEventListener('mouseup', function(e) {
        e.preventDefault();
        cvvInput.type = 'password';
        toggleCvvIcon.classList.remove('fa-eye-slash');
        toggleCvvIcon.classList.add('fa-eye');
    });

    toggleCvvButton.addEventListener('mouseleave', function(e) {
        cvvInput.type = 'password';
        toggleCvvIcon.classList.remove('fa-eye-slash');
        toggleCvvIcon.classList.add('fa-eye');
    });

    // Validazione del CVV
    cvvInput.addEventListener('input', function(e) {
        const isValid = /^[0-9]{3,4}$/.test(e.target.value);
        this.setCustomValidity(isValid ? '' : 'CVV non valido');
        showValidationMessage(this);
    });

    function showValidationMessage(element) {
        if (element.nextElementSibling && element.nextElementSibling.classList.contains('invalid-feedback')) {
            element.nextElementSibling.style.display = element.validationMessage ? 'block' : 'none';
        }
        updateSubmitButton();
    }

    // Simulazione del pagamento
    async function simulatePayment(method, formData) {
        loadingOverlay.style.display = 'flex';

        switch(method) {
            case 'PAYPAL':
                loadingText.textContent = 'Reindirizzamento a PayPal...';
                await new Promise(resolve => setTimeout(resolve, 1500));
                loadingText.textContent = 'Autenticazione PayPal...';
                await new Promise(resolve => setTimeout(resolve, 2000));
                break;

            case 'GOOGLEPAY':
                loadingText.textContent = 'Apertura Google Pay...';
                await new Promise(resolve => setTimeout(resolve, 1000));
                loadingText.textContent = 'Verifica dell\'account Google...';
                await new Promise(resolve => setTimeout(resolve, 1500));
                break;

            case 'CARTA':
                loadingText.textContent = 'Verifica della carta in corso...';
                await new Promise(resolve => setTimeout(resolve, 1500));
                loadingText.textContent = 'Elaborazione del pagamento...';
                await new Promise(resolve => setTimeout(resolve, 1000));
                break;
        }

        loadingText.textContent = 'Completamento del pagamento...';
        await new Promise(resolve => setTimeout(resolve, 1000));

        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({
                    success: true,
                    message: 'Pagamento completato con successo'
                });
            }, 1000);
        });
    }

    // Validazione del form
    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        const activeMethod = document.querySelector('.payment-method.active');
        const paymentMethod = activeMethod.getAttribute('data-method');

        if (paymentMethod === 'CARTA') {
            if (!form.checkValidity() || !validateExpiryDate()) {
                event.stopPropagation();
                form.classList.add('was-validated');
                return;
            }
        }

        const formData = {
            metodoPagamento: paymentMethod,
            idPrenotazione: document.getElementById('idPrenotazione').value,
            totalePagato: document.getElementById('totalePagato').value
        };

        if (paymentMethod === 'CARTA') {
            formData.numeroCarta = numeroCarta.value.replace(/\s/g, '');
            formData.scadenzaGGMM = scadenzaGGMM.value;
            formData.scadenzaAnno = scadenzaAnno.value;
            formData.cardHolder = cardHolder.value;
            formData.cvv = cvvInput.value;
        }

        loadingOverlay.style.display = 'block';
        loadingText.textContent = 'Elaborazione pagamento in corso...';

        try {
            const result = await simulatePayment(paymentMethod, formData);
            if (result.success) {
                // Invia i dati alla servlet
                const response = await fetch('payment', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams(formData)
                });

                const data = await response.json();
                if (data.success) {
                    window.location.href = 'payment-success.jsp';
                } else {
                    throw new Error(data.message);
                }
            }
        } catch (error) {
            console.error('Errore durante il pagamento:', error);
            alert('Si è verificato un errore durante il pagamento: ' + error.message);
        } finally {
            loadingOverlay.style.display = 'none';
        }
    });

    function enableCartaValidation(enable) {
        const cartaInputs = cartaFields.querySelectorAll('input');
        cartaInputs.forEach(input => {
            if (enable) {
                input.setAttribute('required', '');
            } else {
                input.removeAttribute('required');
            }
        });
    }
});