package co.com.s4n.hystrix.dashboard;

import co.com.s4n.hystrix.invocacion_sincrona.CommandHystrixPruebaSincrona;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import play.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Servlet encargado de leer la informacion de metricas de hystrix desde un archivo en disco
 * Created by daniel on 30/10/15.
 */
public class HystrixServlet extends HystrixMetricsStreamServlet {

    /** Constructor por defecto */
    public HystrixServlet() {
        //
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = "/hystrix.stream";

        int delay = 500;
        int batch = 1;

        String data = getFileFromPackage(filename);
        String lines[] = data.split("\n");

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        int batchCount = 0;
        for (;;) {
            for (String s : lines) {
                s = s.trim();
                if (s.length() > 0) {
                    try {
                        response.getWriter().println(s);
                        response.getWriter().println("");
                        response.getWriter().flush();
                        batchCount++;
                    } catch (Exception e) {
                        System.out.println("Exception writing mock data to output.");
                        return;
                    }
                    if (batchCount == batch) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                        batchCount = 0;
                    }
                }
            }
        }
    }

    private String getFileFromPackage(String filename) {
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = HystrixServlet.class.getResourceAsStream(filename);
            br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            StringWriter s = new StringWriter();
            int c = -1;
            while ((c = in.read()) > -1) {
                s.write(c);
            }
            return s.toString();
        }catch (Exception e){
            throw new RuntimeException("Could not find file: " + filename, e);
        }
    }
}