package Redes.Servidor;

// Estructuras de datos
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import java.util.List;

// para archivos
import Redes.ArchivoAlma;


// threads
import java.lang.Thread;

public class PoolAlmacenamiento{
    private final int nHebras;
    private final ListaHebras[] Hebras;
    private final LinkedBlockingQueue<ProcesosAlmacenamiento> Cola;
    public  LinkedList<String> Archivos;
    // para el manejo del archivo almacenamiento
    private ArchivoAlma alma;

    public PoolAlmacenamiento(int nHebras, LinkedList<String> Archivos){
        this.nHebras = nHebras;
        Cola = new LinkedBlockingQueue<ProcesosAlmacenamiento>();
        Hebras = new ListaHebras[nHebras];
        this.Archivos = Archivos;
        this.alma = new ArchivoAlma();
        for (int i=0; i < this.nHebras; i++){
            Hebras[i] = new ListaHebras();
            Hebras[i].start();  
        }
    }

    public LinkedList<String> funcionls(){
        LinkedList<String> disponibles = new LinkedList<String>();
        // diccionario que te mostre antes
        // la idea es crear una clase similar a ArchivoLog
        HashMap<String,List<String>> archivos = alma.getDict();

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
        HashMap<String,List<String>> archivos = alma.getDict(); // igual que antes
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
        List<String> ips = alma.delete(archivo);
        if(ips != null){
            String ip;
            for (int i = 0; i < ips.size(); i++) {
                ip = ips.get(i);
                System.out.println(ip);
                for (ListaHebras var : Hebras) {
                    if( ip.equals(var.getIp())){
                        var.getDelete(archivo + ".part" + i);
                        break;
                    }
                }
                
            }
        }
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
        
        public String getIp(){
            if (proceso != null) {
                // return this.proceso.getIp(); // retorna con puerto
                //System.out.println(this.proceso.socket.getInetAddress());
                return this.proceso.socket.getInetAddress().toString(); // retorna sin puerto
            }
            else{
                return null;
            }
        }

        public void getls(){
            if(proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("ls");
                    proceso.run();
                }                
            }
        }

        public void getDelete(String arch){
            if(proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("delete " + arch);
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