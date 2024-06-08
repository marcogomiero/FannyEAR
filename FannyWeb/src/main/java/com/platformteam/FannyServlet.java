// PUT /cache/put
// curl -X POST "http://localhost:8080/cache/put?key=myKey&value=myValue"

// GET /cache/get
// curl "http://localhost:8080/cache/get?key=myKey"

// DELETE /cache/remove
//curl -X DELETE "http://localhost:8080/cache/remove?key=myKey"


package com.platformteam;

import java.io.IOException;

import com.platformteam.k8s.NamespaceService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.ServletContext;


@WebServlet("/testme")
public class FannyServlet extends HttpServlet {

    private final NamespaceService namespaceService;

    @Autowired
    public FannyServlet(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long delay = Long.valueOf(req.getParameter("delay"));
        String xRoutedBy = req.getHeader("x-routed-by");
        String instana = req.getHeader("x-instana-endpoint");
        String whoAmI = System.getenv("WHO_AM_I");
        String nodeName = System.getenv("NODE_NAME");
        String namespace = System.getenv("NAMESPACE");
        String hostname = System.getenv("HOSTNAME");
        resp.getWriter().write(createResponse(delay, xRoutedBy, whoAmI, nodeName, namespace, hostname, instana));
    }


    private String createResponse(Long delay, String xRoutedBy,
                                                  String whoAmI, String nodeName, String namespace, String hostname, String instanaHeader) {
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
                "  \"K8s NS\":\"" + namespaceService.getCurrentNamespace() + "\",\n" +
                "  \"FRAMEWORK\":\"spring-boot " + org.springframework.boot.SpringBootVersion.getVersion() + "\",\n" +
                "  \"JAVA_VERSION\":\"" + System.getProperty("java.version") + "\"\n" +
                "  \"RUNNING ON\";\"" + serverInfo.getServerInfo() +"\"\n"+
                "}");
    }

}