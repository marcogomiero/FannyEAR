package com.platformteam;

import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/testme")
public class FannyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write("Hello from EJB!\n\n");
        resp.getWriter().write("      ,_,   \n");
        resp.getWriter().write("     {O,o}  \n");
        resp.getWriter().write("     /)__)  \n");
        resp.getWriter().write("     ==\"==\n");
        resp.getWriter().write("FANNY EAR Edition - A PT Project\n");
        resp.getWriter().write("Java Version: " + System.getProperty("java.version") + "\n");

        // Ottieni informazioni sull'application server
        ServletContext context = getServletContext();
        String serverInfo = context.getServerInfo();
        resp.getWriter().write("Application Server: " + serverInfo + "\n");
    }
}