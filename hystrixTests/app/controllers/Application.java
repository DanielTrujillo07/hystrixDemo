package controllers;

import co.com.s4n.hystrix.dashboard.HelloServlet;
import co.com.s4n.hystrix.dashboard.HystrixServlet;
import co.com.s4n.hystrix.invocacion_asincrona.CommandHystrixPruebaASincrona;
import co.com.s4n.hystrix.invocacion_asincrona.Log;
import co.com.s4n.hystrix.invocacion_asincrona.ResultCommandObserver;
import co.com.s4n.hystrix.invocacion_sincrona.CommandHystrixPruebaSincrona;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import play.mvc.*;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * https://github.com/Netflix/Hystrix
 * https://github.com/Netflix/Hystrix/wiki
 */
public class Application extends Controller {

    public Result index() {
        //
        return ok("Your new application is ready.");
    }

    /** Invocacion del ejemplo hystrix de forma sincrona */
    public Result hystrixInvocacionSincrona() {
        StringBuilder logCompleto = new StringBuilder();
        int nroMensajesProcesar = 10;
        CommandHystrixPruebaSincrona command = null;
        for(int i =1; i <= nroMensajesProcesar; i++){
            command = new CommandHystrixPruebaSincrona("{Hola: "+i+"}");
            String mensajeResultado = command.execute();
            System.out.println("Para mensaje: "+i+"; mensaje resultado: "+mensajeResultado);
            logCompleto.append(command.getLog());
            logCompleto.append(mensajeResultado+"\n\n\n");
        }
        return ok("hystrixInvocacionSincrona : \n\n"+logCompleto);
    }

    /** Invocacion del ejemplo hystrix de forma asincrona con rxJava */
    public Result hystrixInvocacionAsincrona() {
        int nroMensajesProcesar = 10;
        CommandHystrixPruebaASincrona command = null;

        for(int i =1; i <= nroMensajesProcesar; i++){
            command = new CommandHystrixPruebaASincrona("{Hola: "+i+"}");
            Observable<String> observable = command.observe();
            observable.observeOn(Schedulers.io()).subscribe(new ResultCommandObserver("{Hola: "+i+"}"));
        }

        try{
            Thread.currentThread().sleep(45000);
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        return ok("hystrixInvocacionAsincrona : \n\n"+Log.getInstance().getLog());
    }

    /**
     * Invocacion del ejemplo hystrix de dashboard
     * 1. Primero se levanta un JETTY (contenedor de servlet), para poder comunicarse con la implemetacion existente de Hystrix dashboard
     * 2. La primera URL lee informacion de referencia desde un archivo fisico (/hystrix.stream)
     * 3. La segunda URL lee informcion desde la ejecucion de Hystrix propia
     */
    public Result hystrixDashboard() {
        try {
            Server server = new Server(9002);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new HelloServlet()), "/*");
            context.addServlet(new ServletHolder(new HelloServlet("Buongiorno Mondo")),"/it/*");
            context.addServlet(new ServletHolder(new HelloServlet("Bonjour le Monde")),"/fr/*");

            // Se agrega la URL para la carga de informacion desde un archivo en disco
            context.addServlet(new ServletHolder(new HystrixServlet()), "/hystrix.stream");
            // Se agrega la URL para la carga de informacion desde una ejecucion Hystrix
            context.addServlet(new ServletHolder(new HystrixMetricsStreamServlet()), "/hystrix2.stream");

            server.start();
            server.join();
            return ok("hystrixDashboard");
        }catch (Exception e){
            return internalServerError("hystrixDashboard: " + e.getMessage());
        }
    }

}