package com.platformteam;

import java.io.IOException;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/get")
public class GetServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String key = req.getParameter("key");
        if (key != null) {
            String value = get(key);
            resp.getWriter().write("Value for key '" + key + "': " + value);
        } else {
            resp.getWriter().write("Key parameter is missing");
        }
    }

    // Metodo per ottenere un valore dalla cache
    public String get(String key) {
        return cache.get(key);
    }
}
