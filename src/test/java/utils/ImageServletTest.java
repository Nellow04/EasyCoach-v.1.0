package utils;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for ImageServlet")
class ImageServletTest {

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private ServletOutputStream mockOutputStream;
    @Mock private ServletConfig mockServletConfig;

    private ImageServlet imageServlet;
    private static final String TEST_FILE_NAME = "test.jpg";
    private static final Path TEST_FILE_PATH = Paths.get(System.getProperty("user.home"), "easycoach_uploads", TEST_FILE_NAME);

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        imageServlet = new ImageServlet();

        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getMimeType(anyString())).thenReturn("image/jpeg");
    }

    @Test
    @DisplayName("TC_1: Richiesta senza path -> SC_NOT_FOUND")
    void testDoGet_NoPath() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn(null);
        imageServlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("TC_2: Richiesta con path '/' -> SC_NOT_FOUND")
    void testDoGet_EmptyPath() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/");
        imageServlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("TC_3: File non trovato -> SC_NOT_FOUND")
    void testDoGet_FileNotFound() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/nonexistent.jpg");
        imageServlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
    }


    @Test
    @DisplayName("TC_6: Errore durante la lettura del file -> Generare IllegalStateException")
    void testDoGet_FileReadError() throws Exception {
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_FILE_NAME);
        Files.createDirectories(TEST_FILE_PATH.getParent());
        Files.write(TEST_FILE_PATH, "testdata".getBytes());

        // Simuliamo un errore di output stream
        doThrow(new IllegalStateException("Errore di test")).when(mockOutputStream).write(any(byte[].class));

        assertThrows(IllegalStateException.class, () -> imageServlet.doGet(mockRequest, mockResponse));

        Files.deleteIfExists(TEST_FILE_PATH);
    }

}
