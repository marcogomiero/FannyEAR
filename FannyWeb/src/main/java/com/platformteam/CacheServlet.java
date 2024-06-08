package com.platformteam;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.platformteam.service.CacheService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/cache")
public class CacheServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CacheServlet.class);

    private CacheService cacheService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cacheService = new CacheService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        logger.info("POST request received with key: {} and value: {}", key, value);
        cacheService.put(key, value);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String key = request.getParameter("key");
        logger.info("GET request received for key: {}", key);
        String value = cacheService.get(key);
        response.setContentType("text/plain");
        try {
            response.getWriter().write(value);
        } catch (Exception e) {
            logger.error("Error while writing response", e);
        }
        return null;
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String key = request.getParameter("key");
        logger.info("DELETE request received for key: {}", key);
        cacheService.remove(key);
    }
}
