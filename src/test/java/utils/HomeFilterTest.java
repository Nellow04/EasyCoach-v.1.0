package utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("Test Cases for HomeFilter")
class HomeFilterTest {

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private FilterChain mockFilterChain;

    private HomeFilter homeFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        homeFilter = new HomeFilter();
    }

    @Test
    @DisplayName("TC_1: Verifica il reindirizzamento a /home")
    void testDoFilter_RedirectToHome() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");

        homeFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(1)).sendRedirect("/home");
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("TC_2: Verifica che il metodo init() non generi eccezioni")
    void testInit_DoesNotThrowException() {
        assertDoesNotThrow(() -> homeFilter.init(null));
    }

    @Test
    @DisplayName("TC_3: Verifica che il metodo destroy() non generi eccezioni")
    void testDestroy_DoesNotThrowException() {
        assertDoesNotThrow(() -> homeFilter.destroy());
    }
}
