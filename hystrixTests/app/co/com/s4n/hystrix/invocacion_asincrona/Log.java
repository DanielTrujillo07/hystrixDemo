package co.com.s4n.hystrix.invocacion_asincrona;

/**
 * Clase utilitaria para capturar los diferentes logs que se presentan en un proceso asincrono
 * @author Daniel Trujillo
 */
public class Log {

    /** Unica instancia de la clase */
    private static Log instance = null;
    /** Atributo que tendra los logs */
    private StringBuilder log = null;

    /** Constructor por defecto de la clase */
    private Log(){
        super();
        log = new StringBuilder();
    }

    /** Metodo encargado de retornar la unica instancia de la clase */
    public static Log getInstance(){
        if(instance == null){
            synchronized (Log.class) {
                if(instance == null){
                    instance = new Log();
                }
            }
        }
        return instance;
    }

    /** Agrega un mensaje al log */
    public void addLog(String msg){
        //
        log.append(msg+"\n");
    }

    /** Retorna el log completo del proceso */
    public String getLog(){
        //
        return log.append("\n").toString();
    }
}
