package codes.erbilcan.springboot.idempotency.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.CacheManager;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class IdempotencyFilterTest {

    HttpServletRequest mockReq;

    HttpServletResponse mockResp;

    FilterChain mockFilterChain;

    FilterConfig mockFilterConfig;

    CacheManager cacheManager;

    IdempotencyFilter idempotencyFilter;

    HandlerExceptionResolver handlerExceptionResolver;

    @BeforeEach
    void setUp() throws IOException, ServletException {

        mockReq = Mockito.mock(HttpServletRequest.class);
        mockResp = Mockito.mock(HttpServletResponse.class);
        mockFilterChain = Mockito.mock(FilterChain.class);
        mockFilterConfig = Mockito.mock(FilterConfig.class);

        cacheManager = Mockito.mock(CacheManager.class);
        handlerExceptionResolver = Mockito.mock(HandlerExceptionResolver.class);

        idempotencyFilter = new IdempotencyFilter(
                cacheManager,
                handlerExceptionResolver,
                "idempotency",
                "X-Idempotency-Key");

        Mockito.when(mockReq.getRequestURI()).thenReturn("/");
        BufferedReader br = new BufferedReader(new StringReader("test"));
        Mockito.when(mockReq.getReader()).thenReturn(br);
        idempotencyFilter.init(mockFilterConfig);
    }

    @Test
    void doFilterInternal_whenHeaderIsNotAvailable_expectException() throws ServletException {
        // Given


        // When
        when(mockReq.getHeader(anyString())).thenReturn(null);

        // Then
        assertThrows(IllegalArgumentException.class,
                () -> idempotencyFilter.doFilterInternal(mockReq, mockResp, mockFilterChain));
    }
}