package co.com.s4n.hystrix.invocacion_sincrona;

/**
 * Clase con el metodo main para ejecutar la prueba sincrona de hystrix
 */
public class Main {

    public static void main (String[] args){
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
        System.out.println("hystrixInvocacionSincrona : \n\n"+logCompleto);
    }
}
