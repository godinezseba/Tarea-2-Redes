package Servidor;

import java.util.concurrent.LinkedBlockingQueue;

import java.lang.Thread;

public class PoolHebras{
    private final int nHebras;
    private final ListaHebras[] Hebras;
    private final LinkedBlockingQueue<Runnable> Cola;

    public PoolHebras(int nHebras){
        this.nHebras = nHebras;
        Cola = new LinkedBlockingQueue<Runnable>();
        Hebras = new ListaHebras[nHebras];
        
        for (int i=0; i < this.nHebras; i++){
            Hebras[i] = new ListaHebras();
            Hebras[i].start();  
        }
    }    
    public void ejecutar(Runnable proceso){
        synchronized(Cola){
            Cola.add(proceso);
            Cola.notify();
        }
    }
    public class ListaHebras extends Thread{
        public void run(){
            Runnable proceso;
            while(true){
                //System.out.println("Ocurre");
                synchronized(Cola){
                    while(Cola.isEmpty()){
                        try{
                            Cola.wait();
                        } catch(InterruptedException e){
                            System.out.println("Ocurrio un error en la espera de la cola "+ e.getMessage());
                        }
                    }
                    proceso = Cola.poll();
                }
                try{
                    proceso.run();
                } catch(RuntimeException e){
                    System.out.println("La ThreadPool ha sido interrumpida"+ e.getMessage());
                }
            }
        }
    }
}