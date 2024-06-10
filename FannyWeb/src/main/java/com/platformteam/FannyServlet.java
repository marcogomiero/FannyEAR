package com.platformteam;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;

import org.springframework.boot.SpringBootVersion;

@WebServlet("/testme")
public class FannyServlet extends HttpServlet {

    private Cache<Object, Object> cache;

    @Override
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String delayParam = req.getParameter("delay");
        Long delay = null;
        if (delayParam != null && !delayParam.isEmpty()) {
            try {
                delay = Long.parseLong(delayParam);
            } catch (NumberFormatException e) {
               delay= 0L;
            }
        }
        String xRoutedBy = req.getHeader("x-routed-by");
        String instana = req.getHeader("x-instana-endpoint");
        String whoAmI = System.getenv("WHO_AM_I");
        String nodeName = System.getenv("NODE_NAME");
        String namespace = System.getenv("NAMESPACE");
        String hostname = System.getenv("HOSTNAME");
        resp.getWriter().write(createResponse(delay, xRoutedBy, whoAmI, nodeName, namespace, hostname, instana));
    }

    private String createResponse(Long delay, String xRoutedBy, String whoAmI, String nodeName, String namespace, String hostname, String instanaHeader) {
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


        DefaultCacheManager cacheManager = null;
        try {
            cacheManager = new DefaultCacheManager("infinispan.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Cache<String, String> cache = cacheManager.getCache("fanny");

        cache.put("key", "value");
        System.out.println("Value from cache: " + cache.get("key"));

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
                "  \"RUNNING ON\":\"" + serverInfo + "\"\n"+
                "}");
    }
}
