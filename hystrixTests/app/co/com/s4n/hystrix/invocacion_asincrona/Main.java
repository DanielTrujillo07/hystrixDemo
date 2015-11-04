package co.com.s4n.hystrix.invocacion_asincrona;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Clase con el metodo main para ejecutar la prueba asincrona de hystrix
 */
public class Main {

    public static void main (String[] args){
        int nroMensajesProcesar = 10;
        CommandHystrixPruebaASincrona command = null;

        for(int i =1; i <= nroMensajesProcesar; i++){
            command = new CommandHystrixPruebaASincrona("{Hola: "+i+"}");
            Observable<String> observable = command.observe();
            observable.observeOn(Schedulers.io()).subscribe(new ResultCommandObserver("{Hola: "+i+"}"));
        }

        try{
            Thread.currentThread().sleep(30000);
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        System.out.println("hystrixInvocacionAsincrona : \n\n"+Log.getInstance().getLog());
    }
}
