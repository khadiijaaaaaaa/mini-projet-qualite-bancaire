package com.hendisantika.onlinebanking.config;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestFilterTest {

    @InjectMocks
    private RequestFilter requestFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private FilterConfig filterConfig;

    // --- TEST 1 : Requête Classique (GET, POST...) ---
    @Test
    void testDoFilter_NormalRequest() throws IOException, ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");

        // Act
        requestFilter.doFilter(request, response, chain);

        // Assert
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        verify(response).setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        verify(chain, times(1)).doFilter(request, response);
    }

    // --- TEST 2 : Requête Pre-flight (OPTIONS) ---
    @Test
    void testDoFilter_OptionsRequest() throws IOException, ServletException {
        // Arrange
        when(request.getMethod()).thenReturn("OPTIONS");

        // Act
        requestFilter.doFilter(request, response, chain);

        // Assert
        verify(response).setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(chain, never()).doFilter(request, response);
    }

    // --- TEST 3 : Méthodes init et destroy ---
    @Test
    void testInitAndDestroy() {
        // CORRECTION SONARQUBE : Ajout d'assertions explicites
        // "Je certifie que l'appel à init() ne doit pas lancer d'erreur"
        assertDoesNotThrow(() -> requestFilter.init(filterConfig));

        // "Je certifie que l'appel à destroy() ne doit pas lancer d'erreur"
        assertDoesNotThrow(() -> requestFilter.destroy());
    }
}