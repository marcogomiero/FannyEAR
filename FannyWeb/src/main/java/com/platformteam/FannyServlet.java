// PUT /cache/put
// curl -X POST "http://localhost:8080/cache/put?key=myKey&value=myValue"

// GET /cache/get
// curl "http://localhost:8080/cache/get?key=myKey"

// DELETE /cache/remove
//curl -X DELETE "http://localhost:8080/cache/remove?key=myKey"


package com.platformteam;

import java.io.IOException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

@WebServlet("/testme")
public class FannyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String delayParam = req.getParameter("delay");
        int delay = 0;
        if (delayParam != null) {
            try {
                delay = Integer.parseInt(delayParam);
            }
            catch (NumberFormatException ignored) {
            }
        }
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Thread was interrupted", e);
            }
        }

        resp.setContentType("text/plain");
        resp.getWriter().write("      ,_,   \n");
        resp.getWriter().write("     {O,o}  \n");
        resp.getWriter().write("     /)__)  \n");
        resp.getWriter().write("     ==\"==\n");
        resp.getWriter().write("FANNY EAR Edition - A PT Project\n");
        resp.getWriter().write("Java Version: " + System.getProperty("java.version") + "\n");

        ServletContext context = getServletContext();
        String serverInfo = context.getServerInfo();
        resp.getWriter().write("Application Server: " + serverInfo + "\n");
        resp.getWriter().write("SpringBoot Version: " + org.springframework.boot.SpringBootVersion.getVersion());
        resp.getWriter().write("\n-> You left me alone for: " + delay + " ms; (you know, this is the delay)\n");
        resp.getWriter().write("-> Feel free to ask for more parameters.\n");
    }

    @Scheduled(fixedRate = 180000)
    public void scheduledCallTestme() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        webClient.get()
                .uri("http://localhost:8080/testme")
                .retrieve()
                .bodyToMono(String.class);
    }
}