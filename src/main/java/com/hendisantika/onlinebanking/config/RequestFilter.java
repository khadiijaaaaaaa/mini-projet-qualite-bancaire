package com.hendisantika.onlinebanking.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * Project : online-banking
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 04/09/18
 * Time: 06.27
 * To change this template use File | Settings | File Templates.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter {

    // 1. Initialisation du Logger
    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                chain.doFilter(req, res);
            } catch (Exception e) {
                // CORRECTION : On utilise le logger pour les erreurs, pas printStackTrace
                logger.error("Erreur inattendue dans le filtre de requête", e);
            }
        } else {
            // CORRECTION : On utilise le logger pour l'info, pas System.out
            logger.info("Pre-flight request detected");

            response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type," +
                    "access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with");
            response.setStatus(HttpServletResponse.SC_OK);
        }

    }

    // CORRECTION (S1185, S1161) : Ajout de @Override et d'un commentaire explicatif
    @Override
    public void init(FilterConfig filterConfig) {
        // Méthode intentionnellement laissée vide. Aucune initialisation spécifique requise.
    }

    // CORRECTION (S1185, S1161) : Ajout de @Override et d'un commentaire explicatif
    @Override
    public void destroy() {
        // Méthode intentionnellement laissée vide. Aucun nettoyage spécifique requis.
    }
}