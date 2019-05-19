package Redes.Servidor;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import java.lang.Thread;

public class PoolAlmacenamiento{
    private final int nHebras;
    private final ListaHebras[] Hebras;
    private final LinkedBlockingQueue<ProcesosAlmacenamiento> Cola;
    public  LinkedList<String> Archivos;

    public PoolAlmacenamiento(int nHebras, LinkedList<String> Archivos){
        this.nHebras = nHebras;
        Cola = new LinkedBlockingQueue<ProcesosAlmacenamiento>();
        Hebras = new ListaHebras[nHebras];
        this.Archivos = Archivos;

        for (int i=0; i < this.nHebras; i++){
            Hebras[i] = new ListaHebras();
            Hebras[i].start();  
        }
    }

    public LinkedList<String> getls(){
        for (ListaHebras var : Hebras) {
            var.getls();
        }

        return this.Archivos;
    }

    public void ejecutar(ProcesosAlmacenamiento proceso){
        synchronized(Cola){
            Cola.add(proceso);
            Cola.notify();
        }
    }
    public class ListaHebras extends Thread{
        ProcesosAlmacenamiento proceso;

        public ListaHebras(){
            proceso = null;
        }

        public void getls(){
            if(proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("ls");
                    proceso.run();
                }                
            }
        }

        public void run(){
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
            }
        }
    }
}