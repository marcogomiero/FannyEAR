/* Decompiler 145ms, total 398ms, lines 50 */
package com.platformteam;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebServlet("/testme")
public class ClusteringDemoServlet extends HttpServlet {
    private static final String TIMESTAMP_KEY = "timestampKey";
    private static final Log log = LogFactory.getLog(ClusteringDemoServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("Processing request");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.info("Cookie info");

            for(int i = 0; i < cookies.length; ++i) {
                log.info("name = " + cookies[i].getName());
                log.info("domain = " + cookies[i].getDomain());
                log.info("val = " + cookies[i].getValue());
            }
        } else {
            log.info("No cookies set!");
        }

        HttpSession session = request.getSession();
        response.encodeURL("/demo/ClusteringDemoServlet");
        Long lastTimestamp = (Long)session.getAttribute("timestampKey");
        PrintWriter writer = response.getWriter();
        writer.append("Last timestamp is ");
        if (lastTimestamp == null) {
            writer.append("null");
        } else {
            writer.append(lastTimestamp.toString());
        }

        writer.append(" If you see the timestamp set from the other server. That means HTTP Session replication is working.");
        Long nextTimestamp = System.currentTimeMillis();
        writer.append(" The next timestamp will be ").append(nextTimestamp.toString());
        session.setAttribute("timestampKey", nextTimestamp);
    }
}