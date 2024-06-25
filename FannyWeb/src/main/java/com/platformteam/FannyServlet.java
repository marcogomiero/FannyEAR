package com.platformteam;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Type;

import jakarta.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletContext;

import org.springframework.boot.SpringBootVersion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebServlet("/testme")
public class FannyServlet extends HttpServlet {

    private Cache<String, String> cache;
    private static final Log log = LogFactory.getLog(FannyServlet.class);
    private static final Gson gson = new Gson();

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
        String delayParam = req.getParameter("delay");
        Long delay = null;
        if (delayParam != null && !delayParam.isEmpty()) {
            try {
                delay = Long.parseLong(delayParam);
            } catch (NumberFormatException e) {
                delay = 0L;
            }
        }
        String xRoutedBy = req.getHeader("x-routed-by");
        String instana = req.getHeader("x-instana-endpoint");
        String whoAmI = System.getenv("WHO_AM_I");
        String nodeName = System.getenv("NODE_NAME");
        String namespace = System.getenv("NAMESPACE");
        String hostname = System.getenv("HOSTNAME");
        String cookieVal = "";

        // Verifica che la cache sia inizializzata
        if (cache != null) {
            // Recupera i cookie dalla cache
            String cachedCookiesJson = cache.get("cookies");

            if (cachedCookiesJson != null) {
                log.info("Retrieved cookies from cache");

                Type cookieListType = new TypeToken<List<Cookie>>() {}.getType();
                List<Cookie> cachedCookies = gson.fromJson(cachedCookiesJson, cookieListType);

                for (Cookie cookie : cachedCookies) {
                    log.info("name = " + cookie.getName());
                    log.info("domain = " + (cookie.getDomain() != null ? cookie.getDomain() : "N/A"));
                    log.info("val = " + cookie.getValue());
                }
            } else {
                // Se i cookie non sono in cache, recuperali dalla richiesta e aggiungili alla cache
                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    log.info("Storing cookies to cache");

                    for (Cookie cookie : cookies) {
                        log.info("name = " + cookie.getName());
                        log.info("domain = " + (cookie.getDomain() != null ? cookie.getDomain() : "N/A"));
                        log.info("val = " + cookie.getValue());
                        cookieVal = cookie.getValue();
                    }

                    String cookiesJson = gson.toJson(Arrays.asList(cookies));
                    cache.put("cookies", cookiesJson);
                } else {
                    log.info("No cookies set!");
                }
            }
        } else {
            log.error("Cache is not initialized");
        }
        resp.getWriter().write(createResponse(delay, xRoutedBy, whoAmI, nodeName, namespace, hostname, instana, cookieVal));
    }

    private String createResponse(Long delay, String xRoutedBy, String whoAmI, String nodeName, String namespace, String hostname, String instanaHeader, String cookeiVal) {
        if (delay != null && delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Error occurred during sleep:\n" + e);
                throw new RuntimeException(e);
            }
        }

        ServletContext context = getServletContext();
        String serverInfo = context.getServerInfo();
        String cacheName = (cache != null) ? cache.getName() : "No cache";

        return ("{\n" +
                "  \"message\": \"Hello World\",\n" +
                "  \"RC\":\"" + "200" + "\",\n" +
                "  \"HEADER\":\"" + xRoutedBy + "\",\n" +
                "  \"INSTANA HEADER\":\"" + instanaHeader + "\",\n" +
                "  \"WHO_AM_I\":\"" + whoAmI + "\",\n" +
                "  \"NODE_NAME\":\"" + nodeName + "\",\n" +
                "  \"HOSTNAME\":\"" + hostname + "\",\n" +
                "  \"NAMESPACE\":\"" + namespace + "\",\n" +
                "  \"ENDPOINT\":\"" + "/testme" + "\",\n" +
                "  \"K8s NS\":\"" + System.getenv("NAMESPACE") + "\",\n" +
                "  \"FRAMEWORK\":\"spring-boot " + SpringBootVersion.getVersion() + "\",\n" +
                "  \"JAVA_VERSION\":\"" + System.getProperty("java.version") + "\",\n" +
                "  \"RUNNING ON\":\"" + serverInfo + "\",\n" +
                "  \"INFINISPAN CACHE IN USE\":\"" + cacheName + "\"\n" +
                "  \"COOKIE VAL\":\"" + cookeiVal + "\"\n" +
                "}");
    }
}
