package com.platformteam;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class IndexController {

    private final NamespaceService namespaceService;
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "500 Internal Server Error";
    private static final String BAD_GATEWAY_MESSAGE = "502 Bad Gateway";

    public IndexController(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public static void main(String[] args) throws IOException {

        // Stampa il banner
        System.out.println("      ,_,   ");
        System.out.println("     {O,o}  ");
        System.out.println("     /)__)  ");
        System.out.println("     ==" + "=\"" + "==");
        System.out.println("FANNY EAR Edition - A PT Project");
        System.out.println("Java Version: " + System.getProperty("java.version"));

        NamespaceService namespaceService = new NamespaceService();
        IndexController controller = new IndexController(namespaceService);

        // Crea il server HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/testme", controller.new TestmeHandler());
        server.createContext("/", controller.new RedirectHandler());
        server.setExecutor(null);
        server.start();

        // Programma l'attività periodica
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(controller.new ScheduledTask(), 0, 180000);

        System.out.println("Server started on port 8080");
    }

    class TestmeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            String delayParam = getQueryParam(exchange, "delay");
            String forceHttpCodeParam = getQueryParam(exchange, "forceHttpCode");

            Long delay = delayParam != null ? Long.parseLong(delayParam) : null;
            Integer forceHttpCode = forceHttpCodeParam != null ? Integer.parseInt(forceHttpCodeParam) : 200;

            long startTime = System.currentTimeMillis();

            if (forceHttpCode == 500) {
                sendResponse(exchange, 500, INTERNAL_SERVER_ERROR_MESSAGE);
                return;
            }
            if (forceHttpCode == 502) {
                sendResponse(exchange, 502, BAD_GATEWAY_MESSAGE);
                return;
            }

            String xRoutedBy = exchange.getRequestHeaders().getFirst("x-routed-by");
            String whoAmI = System.getenv("WHO_AM_I");
            String nodeName = System.getenv("NODE_NAME");
            String namespace = System.getenv("NAMESPACE");
            String hostname = System.getenv("HOSTNAME");

            if (delay != null && delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    System.out.println("Error occurred during sleep:\n" + e);
                    throw new RuntimeException(e);
                }
            }

            String json = "{\n" +
                    "  \"message\": \"Hello World\",\n" +
                    "  \"RC\":\"" + forceHttpCode + "\",\n" +
                    "  \"HEADER\":\"" + xRoutedBy + "\",\n" +
                    "  \"WHO_AM_I\":\"" + whoAmI + "\",\n" +
                    "  \"NODE_NAME\":\"" + nodeName + "\",\n" +
                    "  \"HOSTNAME\":\"" + hostname + "\",\n" +
                    "  \"NAMESPACE\":\"" + namespace + "\",\n" +
                    "  \"ENDPOINT\":\"" + "/testme" + "\",\n" +
                    "  \"K8s NS\":\"" + namespaceService.getCurrentNamespace() + "\",\n" +
                    "  \"FRAMEWORK\":\"traditional java application\",\n" +
                    "  \"JAVA_VERSION\":\"" + System.getProperty("java.version") + "\"\n" +
                    "}";

            sendResponse(exchange, 200, json);

            long elapsedTime = System.currentTimeMillis() - startTime;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            String currentTime = dateFormat.format(new Date());
            String utcTime = utcDateFormat.format(new Date());

            System.out.printf("%s %s  INFO 00001 --- [fanny] com.platformteam.IndexController : URI invocata: %s - Tempo impiegato: %d ms - Evasa dal container: %s%n",
                    currentTime, utcTime, exchange.getRequestURI(), elapsedTime, hostname);
        }

        private String getQueryParam(HttpExchange exchange, String param) {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) return null;
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1 && keyValue[0].equalsIgnoreCase(param)) {
                    return keyValue[1];
                }
            }
            return null;
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class RedirectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }
            exchange.getResponseHeaders().set("Location", "/testme");
            exchange.sendResponseHeaders(302, -1); // Redirect
        }
    }

    class ScheduledTask extends TimerTask {
        @Override
        public void run() {
            // Effettua una chiamata HTTP a /testme
            try {
                java.net.URL url = new java.net.URL("http://localhost:8080/testme");
                try (java.io.InputStream is = url.openStream()) {
                    java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
                    is.transferTo(os);
                    String response = os.toString();
                    logScheduledCall(response);
                }
            } catch (Exception e) {
                System.out.println("Error calling /testme: " + e.getMessage());
            }
        }

        private void logScheduledCall(String response) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            String currentTime = dateFormat.format(new Date());
            String utcTime = utcDateFormat.format(new Date());

            System.out.printf("%s %s  INFO 00001 --- [fanny] com.platformteam.IndexController : WarmedUP - response from /testme OK\n",
                    currentTime, utcTime);
        }
    }
}
