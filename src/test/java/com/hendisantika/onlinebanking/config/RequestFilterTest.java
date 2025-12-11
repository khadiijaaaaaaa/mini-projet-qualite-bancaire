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
        // 1. Vérifie que les headers CORS sont bien ajoutés
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        verify(response).setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");

        // 2. IMPORTANT : Vérifie que la chaîne continue (chain.doFilter est appelé)
        // C'est le comportement attendu pour une requête normale (le 'if')
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
        // 1. Vérifie les headers spécifiques au Pre-flight
        verify(response).setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // 2. IMPORTANT : Vérifie que la chaîne S'ARRÊTE (chain.doFilter N'EST PAS appelé)
        // C'est le comportement attendu pour OPTIONS (le 'else')
        verify(chain, never()).doFilter(request, response);
    }

    // --- TEST 3 : Méthodes init et destroy (pour le 100%) ---
    @Test
    void testInitAndDestroy() {
        // Ces méthodes sont vides mais doivent être appelées pour le coverage
        requestFilter.init(filterConfig);
        requestFilter.destroy();
        // Pas d'assertion spéciale, on vérifie juste que ça ne plante pas
    }
}