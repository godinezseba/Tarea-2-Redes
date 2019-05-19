package Redes.Servidor;

// Estructuras de datos
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Hashtable;
import java.util.List;

// threads
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

    public LinkedList<String> funcionls(){
        LinkedList<String> disponibles;
        // diccionario que te mostre antes
        // la idea es crear una clase similar a ArchivoLog
        Hashtable<String,List<String>> archivos = ArchivoAlma.getDict();

        for (String texto : archivos.keySet()) {
            // reseteamos la lista Archivos
            for(String ip : archivos.get(texto)){
                for (ListaHebras var : Hebras) {
                    // if ip == var.getIp
                    // var.getls
                    // break
                }
            }
            // if listaArchivo == archivos.get(text)
            // agregar texto a la lista disponibles

        }

        return disponibles;
    }

    public void funcionGet(String archivo){
        Hashtable<String,List<String>> archivos = ArchivoAlma.getDict(); // igual que antes
        List<String> ips = archivos.get(archivo);
        // recorremos la lista cada hebra obtendra una parte y aqui las juntamos
        for(String ip : ips){
            for (ListaHebras var : Hebras) {
                // if ip == var.getIp
                // var.getget
                // break
            }
            // agregar parte al archivo
        }
        // deberiamos tener todo el archivo
    }

    public void funcionPut(String archivo){
        // ArchivoAlma.setFile() para generar una linea con el nombre del archivo
        // ver que hebras tenemos disponibles
        // ir hebra por hebra mientras se divide el archivo
        // mientras se guarda hacer ArchivoAlma.setIP(file, ip),
        // para guardar que esta parte esta en esta ip
        // finalmente eliminar el archivo
    }

    public void funcionDelete(String archivo){
        // quizas sea similar a put
        // eliminarlo del archivo
        // ArchivoAlma.delete(file)
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