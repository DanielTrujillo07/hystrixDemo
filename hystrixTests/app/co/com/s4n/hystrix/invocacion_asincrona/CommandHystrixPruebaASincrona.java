package co.com.s4n.hystrix.invocacion_asincrona;

import com.netflix.hystrix.*;

import java.util.Random;

/**
 * Clase encargada de implementar el command Hystrix para la prueba de invocacion sincrona!!!
 * @author Daniel Trujillo
 */
public class CommandHystrixPruebaASincrona extends HystrixCommand<String> {

    /** Mensaje a procesar */
    private String mensaje = null;
    /** Nro de reintentos realizados */
    private int retry = 0;

    /** Constructor por defecto de la clase */
    public CommandHystrixPruebaASincrona(String msg){
        /** Se establece la configuracion inicial del proceso */
        super(getConfiguracio("pool"+msg));
        // Se establece la informacion de configuracion propia del negocio
        this.mensaje = msg;
        this.retry = 0;
        crearLog("Constructor!!!!");
    }

    /** Constructor por defecto de la clase, que recive la URL del servicio core a consumir  */
    public CommandHystrixPruebaASincrona(String msg, int retry){
        /** Se establece la configuracion inicial del proceso */
        super(getConfiguracio("pool"+msg));
        // Se establece la informacion de configuracion propia del negocio
        this.mensaje = msg;
        this.retry = retry;
    }

    /** Metodo encargado de ejecutar la logica de procesamiento de la informacion */
    @Override
    protected String run() throws Exception {
        crearLog("Start RUN");
        return invocarLogicaProcesamiento();
    }
    /** Método encargado de ejcutar la logica del fallback del commandHystrix para realizar el reintento o dar un resultado por defecto */
    @Override
    protected String getFallback() {
        crearLog("Start fallBack");
        // Se aumenta los reintentos
        this.retry ++;
        try {
            // Solo va a realizar 2 reintentos en el fallback, si no retorna un mensjae por defecto!!
            if(retry <= 2){
                // Se ejecuta el exponential backoff
                invocarExponencialBackoff();
                // Si la logica de procesamiento, require invocar un servicio externo a la app, netflix recomienda hacerlo
                // mediante un nuevo comando!!!
                CommandHystrixPruebaASincrona nuevoComando = new CommandHystrixPruebaASincrona(this.mensaje, this.retry);
                // en este punto se deberia ejecutar de forma sincrona, no tiene sentido enviar a procesar de forma asincrona
                String resultado = nuevoComando.execute();
                return resultado;
            }else{
                // Se envia un resultado por defecto
                crearLog("MENSAJE POR DEFECTO DEL FALLBACK!!!");
                return "MENSAJE POR DEFECTO DEL FALLBACK!!! -> "+this.mensaje;
            }
        }catch (Exception e){
            // Se envia un resultado por defecto del error
            crearLog("FALLO EL PROCESAMIENTO EN EL FALLBACK!!!");
            return "FALLO EL PROCESAMIENTO EN EL FALLBACK!!! -> "+this.mensaje;
        }
    }

    /** Metodo encargado de invocar la logica de procesamiento */
    private String invocarLogicaProcesamiento() throws Exception {
        // Para pruebas, se calculara un ramdom de 1 a 10, para "imitar" posbiles fallas en este procesamiento del mensaje
        int random = new Random().nextInt(10);
        //  30% de exito
        if (random <= 3) {
            crearLog("RUN -> random: " + random + " Resultado: EXITOSO");
            return "Resultado: EXITOSO -> " +this.mensaje;
        } else{
            crearLog("RUN -> random: "+random + " Resultado: IR AL FALLBACK!!!");
            throw new Exception("IR AL FALLBACK!!!");
        }
    }

    /** Metodo encargado de ejcutar el backoff */
    private void invocarExponencialBackoff() throws InterruptedException{
        crearLog("Exponecial BACKOFF...");
        int retrySleep = this.retry * 500;
        Thread.sleep(retrySleep);
    }

    /** Metodo encargado de recupear la informacion por defecto de cada command */
    private static Setter getConfiguracio(String poolName) {
        return Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("grupoPrueba")) // Configuracion del grupo para los comandos
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(poolName)) // Configuracion para el pool a utilizar
                    .andCommandPropertiesDefaults(
                            HystrixCommandProperties.Setter()
                                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD) // Forma de ejecucion de hystrix (RECOMENDADA)
                                    .withExecutionTimeoutInMilliseconds(1000) // Time out para el procesamiento de la informacion
                                    .withFallbackIsolationSemaphoreMaxConcurrentRequests(5/*10*/) // Numero de solicitudes concurrentes ejecutando el fallback - default (10)
                                    .withCircuitBreakerEnabled(true) // habilitar circuit breaker
                    )
                    .andThreadPoolPropertiesDefaults(
                            HystrixThreadPoolProperties.Setter()
                                    .withCoreSize(5) // Maximo de comandos a ejecutar de forma concurrentes - default (10)
                                    .withMaxQueueSize(-1) // nro de elementos permitidos en la cola antes de bloquearse, -1 no al limite
                    );
    }

    /** Metodo encargado de crear un log de salida */
    private synchronized void crearLog(String log){
        StringBuilder msg = new StringBuilder(
                "{HILO: " + Thread.currentThread().getName() + "} - " +
                "{MENSAJE PROCESADO: " + this.mensaje + "} - " +
                "{RETRY: " + retry + "} - " +
                "{EVENTO: " + log + "}");
        System.out.println(msg.toString());
        Log.getInstance().addLog(msg.toString());
    }
}