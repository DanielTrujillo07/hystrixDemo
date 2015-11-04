package co.com.s4n.hystrix.invocacion_asincrona;

import rx.Observer;

/**
 * Observador para el resultado del comando hystrix
 * Created by ruben-s4n on 6/08/15.
 */
public class ResultCommandObserver implements Observer<String> {

    private String mensaje;

    public ResultCommandObserver(String msg) {
        this.mensaje = msg;
    }

    @Override
    public void onCompleted() {
        StringBuilder msg = new StringBuilder(
                "{HILO: " + Thread.currentThread().getName() + "} - " +
                "{onCompleted: " + this.mensaje + "}");
        System.out.println(msg.toString());
        Log.getInstance().addLog(msg.toString());
    }

    @Override
    public void onError(Throwable throwable) {
        StringBuilder msg = new StringBuilder(
                "{HILO: " + Thread.currentThread().getName() + "} - " +
                "{onError: " + this.mensaje + "} - " +
                "{throwable: " + throwable + "}");
        System.out.println(msg.toString());
        Log.getInstance().addLog(msg.toString());
    }

    /** Metodo encargado de procesar la informacion que se esta observando */
    @Override
    public void onNext(String msg) {
        StringBuilder m = new StringBuilder(
                "{HILO: " + Thread.currentThread().getName() + "} - " +
                "{onNext: " + this.mensaje + "} - " +
                "{resultado: " + msg + "}");
        System.out.println(m.toString());
        Log.getInstance().addLog(msg.toString());
    }
}