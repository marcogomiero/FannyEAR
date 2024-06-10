package com.platformteam;

import java.io.IOException;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/put")
public class PutServlet extends HttpServlet {

    private Cache<String, String> cache;

    @Override
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        try {
            DefaultCacheManager cacheManager = new DefaultCacheManager("infinispan.xml");
            cache = cacheManager.getCache("fanny");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        if (key != null && value != null) {
            put(key, value);
            resp.getWriter().write("Stored key '" + key + "' with value '" + value + "'");
        } else {
            resp.getWriter().write("Key or value parameter is missing");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        if (key != null && value != null) {
            put(key, value);
            resp.getWriter().write("Stored key '" + key + "' with value '" + value + "'");
        } else {
            resp.getWriter().write("Key or value parameter is missing");
        }
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }
}
