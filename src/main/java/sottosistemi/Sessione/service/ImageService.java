package sottosistemi.Sessione.service;

import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ImageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    /**
     * Valida la Part (immagine) controllando dimensioni e estensione/mime
     */
    public boolean validateImage(Part filePart) {
        if (filePart == null || filePart.getSize() == 0) {
            // Non c'è immagine
            return false;
        }
        if (filePart.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        // Controlla l'estensione (es. .jpg, .png...)
        String submittedFileName = getSubmittedFileName(filePart);
        if (submittedFileName == null) {
            return false;
        }
        String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.') + 1).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Carica il file in 'permanentUploadPath' e ritorna il path relativo da salvare a DB.
     */
    public String processImageUpload(Part filePart, String permanentUploadPath, String uploadDirectory) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String submittedFileName = getSubmittedFileName(filePart);
        if (submittedFileName == null) {
            return null;
        }
        String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.'));
        String newFileName = UUID.randomUUID().toString() + extension;
        String absoluteFilePath = permanentUploadPath + File.separator + newFileName;

        // Copia fisicamente il file
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, Paths.get(absoluteFilePath));
        }

        // Ritorna il path relativo
        return uploadDirectory + File.separator + newFileName;
    }

    /**
     * Elimina il file vecchio, se presente.
     */
    public void deleteImage(String imagePath, String permanentUploadPath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        // estrai il fileName dal path relativo
        String fileName = imagePath.substring(imagePath.lastIndexOf(File.separator) + 1);

        Path filePath = Paths.get(permanentUploadPath, fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Legge il nome del file dalla header "content-disposition"
     */
    public String getSubmittedFileName(Part part) {
        if (part == null) return null;
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }
}
