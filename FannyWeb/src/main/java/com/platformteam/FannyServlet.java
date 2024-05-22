package com.platformteam;

import java.io.IOException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@WebServlet("/testme")
public class FannyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
    }

    @Scheduled(fixedRate = 180000)
    public void scheduledCallTestme() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        Mono<String> response = webClient.get()
                .uri("http://localhost:8080/testme")
                .retrieve()
                .bodyToMono(String.class);
    }
}