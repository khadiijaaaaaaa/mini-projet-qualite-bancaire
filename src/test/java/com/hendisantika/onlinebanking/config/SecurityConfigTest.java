package com.hendisantika.onlinebanking.config;


import com.hendisantika.onlinebanking.service.UserServiceImpl.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private UserSecurityService userSecurityService;

    @Mock
    private AuthenticationManagerBuilder authBuilder;

    // --- TEST 1 : Vérifier le Bean PasswordEncoder ---
    @Test
    void testPasswordEncoder() {
        // Act
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(encoder, "Le PasswordEncoder ne doit pas être null");

        // On vérifie qu'il fonctionne (encodage basique)
        String rawPassword = "test";
        String encoded = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
    }

    // --- TEST 2 : Vérifier la configuration globale (AuthenticationManagerBuilder) ---
    @Test
    void testConfigureGlobal() throws Exception {
        // 1. Créer un mock pour l'objet intermédiaire retourné par userDetailsService
        // On utilise le type brut (raw type) pour éviter les problèmes de génériques
        DaoAuthenticationConfigurer daoConfigurer = mock(DaoAuthenticationConfigurer.class);

        // 2. Configurer le comportement de la chaîne avec doReturn (plus sûr pour les types)

        // "Quand on appelle userDetailsService, retourne le configurer"
        doReturn(daoConfigurer).when(authBuilder).userDetailsService(any());

        // "Quand on appelle passwordEncoder sur le configurer, retourne le configurer"
        doReturn(daoConfigurer).when(daoConfigurer).passwordEncoder(any());

        // Act
        securityConfig.configureGlobal(authBuilder);

        // Assert
        // Vérifie que la chaîne a bien été exécutée :
        // 1. Appel du service
        verify(authBuilder, times(1)).userDetailsService(userSecurityService);
        // 2. Appel de l'encodeur sur le résultat
        verify(daoConfigurer, times(1)).passwordEncoder(any(BCryptPasswordEncoder.class));
    }
}