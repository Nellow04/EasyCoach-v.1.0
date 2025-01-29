package sottosistemi.Sessione.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SessionValidationService {

    // Regex usate in entrambe le servlet
    private static final Pattern TITLE_REGEX = Pattern.compile("^.{2,25}$");
    private static final Pattern DESCRIPTION_REGEX = Pattern.compile("^.{2,250}$");
    private static final Pattern PRICE_REGEX = Pattern.compile("^(?!0+([.,]0*)?$)(?!0*[.,]0*$)\\d{1,3}(?:[.,]\\d{1,2})?$");

    /**
     * Valida il titolo (2-25 caratteri).
     */
    public boolean validateTitle(String title) {
        return title != null && TITLE_REGEX.matcher(title.trim()).matches();
    }

    /**
     * Valida la descrizione (2-250 caratteri).
     */
    public boolean validateDescription(String description) {
        return description != null && DESCRIPTION_REGEX.matcher(description.trim()).matches();
    }

    /**
     * Valida il prezzo con la regex definita sopra (max 3 cifre + 2 decimali, >0).
     */
    public boolean validatePrice(String priceStr) {
        if (priceStr == null || !PRICE_REGEX.matcher(priceStr).matches()) {
            return false;
        }
        double price = Double.parseDouble(priceStr.replace(',', '.'));
        return price > 0 && price <= 999;
    }

    /**
     * Valida i timeslot (array di giorni e ore).
     * Esempio: days e hours non null, lunghezza > 0, e corrispondente (days.length == hours.length).
     */
    public boolean validateTimeslots(String[] days, String[] hours) {
        if (days == null || hours == null || days.length == 0 || days.length != hours.length) {
            return false;
        }

        // Esempio: controlliamo che day sia compreso tra 0 e 6, ora tra 0 e 23
        for (String day : days) {
            try {
                int dayValue = Integer.parseInt(day);
                if (dayValue < 0 || dayValue > 6) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        for (String hour : hours) {
            try {
                int hourValue = Integer.parseInt(hour);
                if (hourValue < 0 || hourValue > 23) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo “completo” per validare tutti i campi (CreateSession).
     * Ritorna una mappa (field -> messaggio di errore).
     */
    public Map<String, String> validateForm(String title, String description, String priceStr,
                                            String[] days, String[] hours, boolean imageProvided) {
        Map<String, String> errors = new HashMap<>();

        if (!validateTitle(title)) {
            errors.put("titolo", "Il titolo deve essere lungo tra 2 e 25 caratteri");
        }
        if (!validateDescription(description)) {
            errors.put("descrizione", "La descrizione deve essere lunga tra 2 e 250 caratteri");
        }
        if (!validatePrice(priceStr)) {
            errors.put("prezzo", "Il prezzo deve essere un numero valido tra 0 e 999 (max 2 decimali)");
        }
        if (!validateTimeslots(days, hours)) {
            errors.put("timeslots", "Seleziona almeno un timeslot valido");
        }
        if (!imageProvided) {
            errors.put("immagine", "L'immagine è obbligatoria, formato (JPEG, PNG, GIF) max 10MB");
        }

        return errors;
    }

    /**
     * Variante di validateForm per EditSession (dove l'immagine può essere assente).
     */
    public Map<String, String> validateFormEdit(String title, String description,
                                                String priceStr, String[] days, String[] hours) {
        Map<String, String> errors = new HashMap<>();

        if (!validateTitle(title)) {
            errors.put("nome", "Il titolo deve essere tra 2 e 25 caratteri");
        }
        if (!validateDescription(description)) {
            errors.put("descrizione", "La descrizione deve essere tra 2 e 250 caratteri");
        }
        if (!validatePrice(priceStr)) {
            errors.put("prezzo", "Il prezzo non è valido (max 999, due decimali)");
        }
        if (!validateTimeslots(days, hours)) {
            errors.put("timeslots", "Seleziona almeno un timeslot valido");
        }

        return errors;
    }
}
