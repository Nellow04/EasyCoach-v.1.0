// search.js
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const resultsDiv = document.getElementById('searchResults');
    const sortSelect = document.getElementById('sortSelect');

    let originalSessions = [];
    let currentSessions = [];


    searchInput.addEventListener('keyup', function(event) {
        if (event.key === 'Enter') {
            performSearch();
        }
    });


    sortSelect.addEventListener('change', function() {
        applySort(sortSelect.value);
    });

    async function performSearch() {
        const query = searchInput.value.trim();
        if (!query) {

            resultsDiv.innerHTML = '';
            originalSessions = [];
            currentSessions = [];
            return;
        }

        try {

            const response = await fetch(`SearchSessionServlet?q=${encodeURIComponent(query)}`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }


            const sessions = await response.json();


            originalSessions = sessions.slice();
            currentSessions = sessions.slice();


            renderResults(currentSessions);
        } catch (error) {
            console.error('Errore:', error);
            resultsDiv.innerHTML = '<p class="ec-error">Si è verificato un errore durante la ricerca.</p>';
        }
    }

    function applySort(sortMode) {
        if (!currentSessions || currentSessions.length === 0) {
            return;
        }


        currentSessions = originalSessions.slice();

        switch (sortMode) {
            case 'priceAsc':
                currentSessions.sort((a, b) => a.prezzo - b.prezzo);
                break;
            case 'priceDesc':
                currentSessions.sort((a, b) => b.prezzo - a.prezzo);
                break;

            default:

                break;
        }

        renderResults(currentSessions);
    }

    function renderResults(sessions) {
        if (!Array.isArray(sessions) || sessions.length === 0) {
            resultsDiv.innerHTML = '<p class="ec-no-results">Nessuna sessione trovata.</p>';
            return;
        }

        let html = '';
        sessions.forEach((s, index) => {
            const titolo = safeText(s.titolo);
            const desc = safeText(s.descrizione);
            const prezzo = s.prezzo || 0;
            const immagine = safeText(s.immagine);
            const status = safeText(s.statusSessione) || '';

            const delayClass = `anim-delay-${Math.min(index, 3)}`;

            html += `
            <div class="ec-card ${delayClass}" 
                 onclick="window.location.href='sessionDetails.jsp?idSessione=${s.idSessione}'">
                
                ${immagine ? `<img src="${immagine}" alt="${titolo}">` : ''}
                
                <div class="ec-card-content">
                    <h2 class="ec-card-title">${titolo}</h2>
                    <p class="ec-card-desc">${desc}</p>
                    <p class="ec-card-price">€${prezzo.toFixed(2)}</p>
                </div>
            </div>
        `;
        });

        resultsDiv.innerHTML = html;
    }


    // Minimal text escaping to avoid HTML injection
    function safeText(str) {
        if (!str) return '';
        return str.replace(/[&<>"']/g, function(m) {
            switch(m) {
                case '&': return '&amp;';
                case '<': return '&lt;';
                case '>': return '&gt;';
                case '"': return '&quot;';
                case '\'': return '&#39;';
                default: return m;
            }
        });
    }
});
