package sottosistemi.Sessione.service;

import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("ImageService Tests")
class ImageServiceTest {

    private ImageService imageService;

    @Mock
    private Part mockPart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService();
    }

    @Test
    @DisplayName("TC_5.1: validateImage con immagine valida")
    void testValidateImage_ValidImage() {
        when(mockPart.getSize()).thenReturn(5 * 1024 * 1024L); // 5 MB
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"image.jpg\"");

        boolean isValid = imageService.validateImage(mockPart);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("TC_5.2: validateImage con filePart nullo")
    void testValidateImage_NullPart() {
        boolean isValid = imageService.validateImage(null);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("TC_5.3: validateImage con dimensione file troppo grande")
    void testValidateImage_FileTooLarge() {
        when(mockPart.getSize()).thenReturn(15 * 1024 * 1024L); // 15 MB

        boolean isValid = imageService.validateImage(mockPart);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("TC_5.4: validateImage con estensione non valida")
    void testValidateImage_InvalidExtension() {
        when(mockPart.getSize()).thenReturn(5 * 1024 * 1024L); // 5 MB
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"image.exe\"");

        boolean isValid = imageService.validateImage(mockPart);

        assertFalse(isValid);
    }



    // Test per ridondanti nel TCS ma utili per garantire la branch coverage
    @Test
    @DisplayName("TC_2.1: processImageUpload con immagine valida")
    void testProcessImageUpload_ValidImage() throws IOException {
        when(mockPart.getSize()).thenReturn(5 * 1024 * 1024L); // 5 MB
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"image.jpg\"");
        when(mockPart.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        String permanentPath = "test_uploads";
        String uploadDir = "uploads";

        Files.createDirectories(Paths.get(permanentPath));

        String result = imageService.processImageUpload(mockPart, permanentPath, uploadDir);

        assertNotNull(result);
        assertTrue(result.startsWith(uploadDir));

        // Cleanup
        Files.deleteIfExists(Paths.get(permanentPath, result.substring(uploadDir.length() + 1)));
    }

    @Test
    @DisplayName("TC_2.2: processImageUpload con filePart nullo")
    void testProcessImageUpload_NullPart() throws IOException {
        String result = imageService.processImageUpload(null, "test_uploads", "uploads");

        assertNull(result);
    }

    @Test
    @DisplayName("TC_2.3: processImageUpload genera IOException durante il salvataggio")
    void testProcessImageUpload_SaveError() throws IOException {
        // Configura il mock per simulare un'eccezione durante il salvataggio
        when(mockPart.getSize()).thenReturn(5 * 1024 * 1024L); // 5 MB
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"image.jpg\"");
        when(mockPart.getInputStream()).thenThrow(new IOException("Errore durante il salvataggio"));

        // Verifica che venga lanciata l'eccezione durante l'esecuzione del metodo
        assertThrows(IOException.class, () -> {
            imageService.processImageUpload(mockPart, "test_uploads", "uploads");
        });
    }

    @Test
    @DisplayName("TC_2.4: processImageUpload con filePart nullo")
    void testProcessImageUpload_NullFilePart() throws IOException {
        String result = imageService.processImageUpload(null, "test_uploads", "uploads");
        assertNull(result, "Il risultato dovrebbe essere null quando filePart è nullo");
    }

    @Test
    @DisplayName("TC_2.5: processImageUpload con submittedFileName nullo")
    void testProcessImageUpload_NullSubmittedFileName() throws IOException {
        // Simula un header content-disposition non nullo ma senza filename
        when(mockPart.getSize()).thenReturn(5 * 1024 * 1024L); // File valido
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\""); // Nessun filename presente

        // Chiama il metodo
        String result = imageService.processImageUpload(mockPart, "test_uploads", "uploads");

        // Verifica che il risultato sia null
        assertNull(result, "Il risultato dovrebbe essere null quando il filename non è presente nell'header content-disposition");
    }

    @Test
    @DisplayName("TC_3.1: deleteImage con immagine esistente")
    void testDeleteImage_ExistingImage() throws IOException {
        String permanentPath = "test_uploads";
        Files.createDirectories(Paths.get(permanentPath));

        String fileName = "test_image.jpg";
        Path filePath = Paths.get(permanentPath, fileName);
        Files.createFile(filePath);

        imageService.deleteImage(fileName, permanentPath);

        assertFalse(Files.exists(filePath));
    }

    @Test
    @DisplayName("TC_3.2: deleteImage con immagine non esistente")
    void testDeleteImage_NonExistingImage() {
        String permanentPath = "test_uploads";
        String fileName = "non_existing.jpg";

        assertDoesNotThrow(() -> imageService.deleteImage(fileName, permanentPath));
    }

    @Test
    @DisplayName("TC_3.3: deleteImage con imagePath nullo o vuoto")
    void testDeleteImage_NullOrEmptyPath() {
        assertDoesNotThrow(() -> imageService.deleteImage(null, "test_uploads"));
        assertDoesNotThrow(() -> imageService.deleteImage("", "test_uploads"));
    }
    @Test
    @DisplayName("TC_4.1: getSubmittedFileName con header valido")
    void testGetSubmittedFileName_ValidHeader() {
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"image.jpg\"");

        String fileName = imageService.getSubmittedFileName(mockPart);

        assertEquals("image.jpg", fileName);
    }

    @Test
    @DisplayName("TC_4.2: getSubmittedFileName con header senza filename")
    void testGetSubmittedFileName_NoFilename() {
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"");

        String fileName = imageService.getSubmittedFileName(mockPart);

        assertNull(fileName);
    }

    @Test
    @DisplayName("TC_4.3: getSubmittedFileName con part nullo")
    void testGetSubmittedFileName_NullPart() {
        String fileName = imageService.getSubmittedFileName(null);

        assertNull(fileName);
    }

}
